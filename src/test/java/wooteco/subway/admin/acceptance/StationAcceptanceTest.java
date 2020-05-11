package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class StationAcceptanceTest {
    @LocalServerPort
    int port;
    TestSupport testSupport;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        testSupport = new TestSupport();
    }

    public static RequestSpecification given() {
        return RestAssured.given().log().all();
    }

    @DisplayName("지하철역을 관리한다")
    @Test
    void manageStation() {
        testSupport.createStation("잠실역");
        testSupport.createStation("종합운동장역");
        testSupport.createStation("선릉역");
        testSupport.createStation("강남역");

        List<StationResponse> stations = getStations();
        assertThat(stations.size()).isEqualTo(4);

        deleteStation(stations.get(0).getId());

        List<StationResponse> stationsAfterDelete = getStations();
        assertThat(stationsAfterDelete.size()).isEqualTo(3);
    }


    private List<StationResponse> getStations() {
        return given().
            when().
            get("/stations").
            then().
            log().all().
            extract().
            jsonPath().getList(".", StationResponse.class);
    }

    private void deleteStation(Long id) {
        given().
            when().
            delete("/stations/" + id).
            then().
            log().all();
    }
}
