package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import io.restassured.RestAssured;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.admin.acceptance.handler.StationHandler;
import wooteco.subway.admin.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Autowired
    private StationHandler stationHandler;

    @DisplayName("지하철역을 관리한다")
    @Test
    void manageStation() {
        stationHandler.createStation("잠실역");
        stationHandler.createStation("종합운동장역");
        stationHandler.createStation("선릉역");
        stationHandler.createStation("강남역");

        List<StationResponse> stations = stationHandler.getStations();
        assertThat(stations.size()).isEqualTo(4);

        stationHandler.deleteStation(stations.get(0).getId());

        List<StationResponse> stationsAfterDelete = stationHandler.getStations();
        assertThat(stationsAfterDelete.size()).isEqualTo(3);
    }
}
