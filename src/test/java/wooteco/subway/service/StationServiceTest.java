package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.info.StationDto;

public class StationServiceTest {
    private StationService stationService;
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new FakeSectionDao();
        stationService = new StationService(new FakeStationDao(), sectionDao);
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        StationDto stationDtoToRequest = new StationDto("강남역");
        StationDto stationDtoToResponse = stationService.save(stationDtoToRequest);
        assertThat(stationDtoToResponse.getName()).isEqualTo(stationDtoToRequest.getName());
    }

    @DisplayName("중복된 이름의 지하철역을 생성 요청 시 예외를 던진다.")
    @Test
    void createStationWithDuplicateName() {
        StationDto stationDto = new StationDto("강남역");
        stationService.save(stationDto);

        assertThatThrownBy(() -> stationService.save(stationDto)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("중복된 지하철 역 이름입니다.");
    }

    @DisplayName("모든 지하철역을 조회한다.")
    @Test
    void getStations() {
        StationDto stationDtoToRequest = new StationDto("강남역");
        StationDto stationDtoToRequest2 = new StationDto("선릉역");
        stationService.save(stationDtoToRequest);
        stationService.save(stationDtoToRequest2);

        assertThat(stationService.findAll()).hasSize(2);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() {
        StationDto stationDtoToRequest = new StationDto("강남역");
        StationDto stationDtoToResponse = stationService.save(stationDtoToRequest);
        stationService.delete(stationDtoToResponse.getId());

        assertThat(stationService.findAll()).hasSize(0);
    }

    @DisplayName("존재하지 않는 id의 지하철역 삭제 요청 시 예외를 던진다.")
    @Test
    void deleteStationNotExists() {
        assertThatThrownBy(() -> stationService.delete(1L)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 지하철 역입니다.");
    }

    @DisplayName("이미 Section에서 사용 중인 지하철역 삭제 요청 시 예외를 던진다.")
    @Test
    void deleteStation_alreadyUsed() {
        StationDto stationDto1 = new StationDto("강남역");
        StationDto stationDto2 = new StationDto("선릉역");
        stationService.save(stationDto1);
        stationService.save(stationDto2);
        sectionDao.save(1L, new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10));

        assertThatThrownBy(() -> stationService.delete(1L)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("해당 역을 지나는 노선이 있으므로 삭제가 불가합니다.");
    }
}
