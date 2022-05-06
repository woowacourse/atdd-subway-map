package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.dto.response.StationResponse;

public class StationServiceTest {
    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(new FakeStationDao());
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        StationRequest stationRequest = new StationRequest("강남역");
        StationResponse stationResponse = stationService.save(stationRequest);
        assertThat(stationResponse.getName()).isEqualTo(stationRequest.getName());
    }

    @DisplayName("중복된 이름의 지하철역을 생성 요청 시 예외를 던진다.")
    @Test
    void createStationWithDuplicateName() {
        StationRequest stationRequest = new StationRequest("강남역");
        stationService.save(stationRequest);
        assertThatThrownBy(() -> stationService.save(stationRequest)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("중복된 지하철 역 이름입니다.");
    }

    @DisplayName("모든 지하철역을 조회한다.")
    @Test
    void getStations() {
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("선릉역");
        stationService.save(stationRequest1);
        stationService.save(stationRequest2);

        assertThat(stationService.findAll()).hasSize(2);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() {
        StationRequest stationRequest = new StationRequest("강남역");
        StationResponse stationResponse = stationService.save(stationRequest);
        stationService.delete(stationResponse.getId());

        assertThat(stationService.findAll()).hasSize(0);
    }

    @DisplayName("존재하지 않는 id의 지하철역 삭제 요청 시 예외를 던진다.")
    @Test
    void deleteStationNotExists() {
        assertThatThrownBy(() -> stationService.delete(1L)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 지하철 역입니다.");
    }
}
