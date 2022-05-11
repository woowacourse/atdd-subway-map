package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.info.StationInfo;

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
        StationInfo stationInfoToRequest = new StationInfo("강남역");
        StationInfo stationInfoToResponse = stationService.save(stationInfoToRequest);
        assertThat(stationInfoToResponse.getName()).isEqualTo(stationInfoToRequest.getName());
    }

    @DisplayName("중복된 이름의 지하철역을 생성 요청 시 예외를 던진다.")
    @Test
    void createStationWithDuplicateName() {
        StationInfo stationInfo = new StationInfo("강남역");
        stationService.save(stationInfo);

        assertThatThrownBy(() -> stationService.save(stationInfo)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("중복된 지하철 역 이름입니다.");
    }

    @DisplayName("모든 지하철역을 조회한다.")
    @Test
    void getStations() {
        StationInfo stationInfoToRequest = new StationInfo("강남역");
        StationInfo stationInfoToRequest2 = new StationInfo("선릉역");
        stationService.save(stationInfoToRequest);
        stationService.save(stationInfoToRequest2);

        assertThat(stationService.findAll()).hasSize(2);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() {
        StationInfo stationInfoToRequest = new StationInfo("강남역");
        StationInfo stationInfoToResponse = stationService.save(stationInfoToRequest);
        stationService.delete(stationInfoToResponse.getId());

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
        StationInfo stationInfo1 = new StationInfo("강남역");
        StationInfo stationInfo2 = new StationInfo("선릉역");
        stationService.save(stationInfo1);
        stationService.save(stationInfo2);
        sectionDao.save(1L, new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10));

        assertThatThrownBy(() -> stationService.delete(1L)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("해당 역을 지나는 노선이 있으므로 삭제가 불가합니다.");
    }
}
