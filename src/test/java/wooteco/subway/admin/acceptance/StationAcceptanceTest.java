package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
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

    public static RequestSpecification given() {
        return RestAssured.given().log().all();
    }

    @TestFactory
    public Stream<DynamicTest> stationTest() {
        return Stream.of(
            DynamicTest.dynamicTest("create station Test", () -> {
                //given
                createStation("잠실역");
                createStation("종합운동장역");
                createStation("선릉역");
                createStation("강남역");

                //when
                List<StationResponse> stations = getStations();

                //then
                assertThat(stations.size()).isEqualTo(4);
            }),
            DynamicTest.dynamicTest("delete station test", () -> {
                //given
                List<StationResponse> stations = getStations();

                //when
                deleteStation(stations.get(0).getId());

                //then
                List<StationResponse> stationsAfterDelete = getStations();
                assertThat(stationsAfterDelete.size()).isEqualTo(3);
            })
        );
    }

    private void createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/api/stations").
            then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }

    private List<StationResponse> getStations() {
        return given().
            when().
            get("/api/stations").
            then().
                    log().all().
                    extract().
                    jsonPath().getList(".", StationResponse.class);
    }

    private void deleteStation(Long id) {
        given().
            when().
            delete("/api/stations/" + id).
            then().
                log().all();
    }
}
