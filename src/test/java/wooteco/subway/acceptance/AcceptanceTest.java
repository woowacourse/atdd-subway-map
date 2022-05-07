package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        sectionRepository.deleteAll();
        lineRepository.deleteAll();
        stationRepository.deleteAll();
    }

    protected ExtractableResponse<Response> requestCreateStation(String stationName) {
        return RestAssured.given().log().all()
            .body(Map.of("name", stationName))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> requestCreateLine(String name, String color,
                                                              Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = Map.of(
            "name", name,
            "color", color,
            "upStationId", String.valueOf(upStationId),
            "downStationId", String.valueOf(downStationId),
            "distance", String.valueOf(distance));

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }
}
