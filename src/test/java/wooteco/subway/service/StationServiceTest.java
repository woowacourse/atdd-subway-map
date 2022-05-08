package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@SpringBootTest
@Sql("/truncate.sql")
class StationServiceTest {

    private final StationService stationService;

    @Autowired
    public StationServiceTest(StationService stationService) {
        this.stationService = stationService;
    }

    @DisplayName("지하쳘역을 저장한다.")
    @Test
    void 지하철역_저장() {
        String name = "선릉역";
        StationRequest stationRequest = new StationRequest(name);

        StationResponse stationResponse = stationService.save(stationRequest);

        assertThat(stationResponse.getName()).isEqualTo(name);
    }
}
