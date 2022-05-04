package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

class StationServiceTest {

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(new FakeStationDao());
    }

    @Test
    @DisplayName("역을 생성한다.")
    void create() {
        // given
        StationRequest request = new StationRequest("강남역");

        // when
        StationResponse response = stationService.create(request);

        // then
        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @Test
    @DisplayName("모든 역을 조회한다.")
    void findAll() {
        // given
        stationService.create(new StationRequest("노원역"));
        stationService.create(new StationRequest("왕십리역"));

        // when
        List<StationResponse> stationResponses = stationService.findAll();

        // then
        assertThat(stationResponses).hasSize(2);
    }
}
