package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.StationRequestDto;
import wooteco.subway.exception.CanNotDeleteException;
import wooteco.subway.exception.DuplicateStationNameException;
import wooteco.subway.mockDao.MockSectionDao;
import wooteco.subway.mockDao.MockStationDao;
import wooteco.subway.repository.entity.SectionEntity;

class StationServiceTest {

    private final MockStationDao mockStationDao = new MockStationDao();
    private final MockSectionDao mockSectionDao = new MockSectionDao();

    private final StationService stationService = new StationService(mockStationDao, mockSectionDao);

    StationRequestDto stationRequestDto_SL = new StationRequestDto("선릉역");
    StationRequestDto stationRequestDto_GN = new StationRequestDto("강남역");
    StationRequestDto stationRequestDto_JS = new StationRequestDto("잠실역");

    @BeforeEach
    void setUp() {
        mockStationDao.removeAll();
        mockSectionDao.removeAll();
    }

    @DisplayName("역 이름을 입력받아서 해당 이름을 가진 역을 등록한다.")
    @Test
    void register() {
        final Station created = stationService.register(stationRequestDto_SL);

        assertThat(created.getName()).isEqualTo("선릉역");
    }

    @DisplayName("이미 존재하는 역이름으로 등록하려할 시 예외가 발생한다.")
    @Test
    void registerDuplicateName() {
        stationService.register(stationRequestDto_SL);

        assertThatThrownBy(() -> stationService.register(stationRequestDto_SL))
                .isInstanceOf(DuplicateStationNameException.class)
                .hasMessage("[ERROR] 이미 존재하는 역 이름입니다.");
    }

    @DisplayName("등록된 모든 역 리스트를 조회한다.")
    @Test
    void searchAll() {
        stationService.register(stationRequestDto_SL);
        stationService.register(stationRequestDto_GN);
        stationService.register(stationRequestDto_JS);

        List<Station> stations = stationService.searchAll();
        List<String> names = stations.stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        assertThat(names).isEqualTo(List.of("선릉역", "강남역", "잠실역"));
    }

    @DisplayName("id 로 역을 삭제한다.")
    @Test
    void remove() {
        stationService.register(stationRequestDto_JS);
        Station station = stationService.register(stationRequestDto_GN);

        stationService.remove(station.getId());

        assertThat(stationService.searchAll().size()).isEqualTo(1);
    }

    @DisplayName("역이 구간으로 사용되었다면 예외발생가 발생한다.")
    @Test
    void removeUsed() {
        Station station1 = stationService.register(stationRequestDto_SL);
        Station station2 = stationService.register(stationRequestDto_GN);
        mockSectionDao.save(new SectionEntity(1L, 1L, station1.getId(), station2.getId(), 100));

        assertThatThrownBy(() -> stationService.remove(station1.getId()))
                .isInstanceOf(CanNotDeleteException.class)
                .hasMessage("[ERROR] 삭제 할 수 없습니다.");
    }
}
