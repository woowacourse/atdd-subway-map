package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql("/truncate.sql")
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    public static RequestSpecification given() {
        return RestAssured.given().log().all();
    }

    @DisplayName("지하철역을 관리한다")
    @Test
    void manageStation() {
        // given
        createStation("잠실역");
        createStation("종합운동장역");
        createStation("선릉역");
        createStation("강남역");
        // when
        List<StationResponse> stations = getStations();
        // then
        assertThat(stations.size()).isEqualTo(4);
        // when
        deleteStation(stations.get(0).getId());
        // then
        List<StationResponse> stationsAfterDelete = getStations();
        assertThat(stationsAfterDelete.size()).isEqualTo(3);
    }

    private void createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        // @formatter:off
        given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .post("/stations")
        .then()
            .log().all()
            .statusCode(HttpStatus.CREATED.value());
        // @formatter:on
    }

    private List<StationResponse> getStations() {
        // @formatter:off
        return given()
        .when()
            .get("/stations")
        .then()
            .log().all()
            .extract()
            .jsonPath()
            .getList(".", StationResponse.class);
        // @formatter:on
    }

    private void deleteStation(Long id) {
        // @formatter:off
        given()
        .when()
            .delete("/stations/" + id)
        .then()
            .log().all();
        // @formatter:on
    }
}
