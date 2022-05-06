package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

class StationServiceTest {

    private StationService stationService;
    private StationDao fakeStationDao;

    @BeforeEach
    void setUp() {
        fakeStationDao = new FakeStationDao();
        stationService = new StationService(fakeStationDao);
    }

    @Test
    @DisplayName("역을 생성한다.")
    void Create() {
        // given
        final StationRequest request = new StationRequest("강남역");

        // when
        final StationResponse response = stationService.create(request);

        // then
        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @Test
    @DisplayName("저장하려는 역 이름이 중복되면 예외를 던진다.")
    void Create_DuplicateName_ExceptionThrown() {
        // given
        final String name = "강남역";
        fakeStationDao.save(new Station(name));

        final StationRequest request = new StationRequest(name);

        // then
        assertThatThrownBy(() -> stationService.create(request))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("중복된 이름의 역은 저장할 수 없습니다.");
    }

    @Test
    @DisplayName("모든 역을 조회한다.")
    void FindAll() {
        // given
        fakeStationDao.save(new Station("노원역"));
        fakeStationDao.save(new Station("왕십리역"));

        // when
        final List<StationResponse> stationResponses = stationService.findAll();

        // then
        assertThat(stationResponses).hasSize(2);
    }

    @Test
    @DisplayName("id에 해당하는 역을 삭제한다.")
    void Delete() {
        // given
        final Station savedStation = fakeStationDao.save(new Station("마들역")).orElseThrow();

        // when
        stationService.delete(savedStation.getId());

        // then
        final List<Station> remainStations = fakeStationDao.findAll();
        assertThat(remainStations).hasSize(0);
    }
}
