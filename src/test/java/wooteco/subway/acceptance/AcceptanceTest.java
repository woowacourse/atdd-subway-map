package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineSaveRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AcceptanceTest {

    protected static final String NOT_FOUND_ERROR_MESSAGE = "존재하지 않습니다";
    protected static final String BLANK_OR_NULL_ERROR_MESSAGE = "빈 값";

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    protected ExtractableResponse<Response> createStationAndReturnResponse(final String stationName) {
        return RestAssured.given()
                .body(new StationRequest(stationName))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().extract();
    }

    protected StationResponse createStation(final String stationName) {
        return RestAssured.given()
                .body(new StationRequest(stationName))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().extract()
                .as(StationResponse.class);
    }

    protected LineResponse createLine(
            final String name, final String color, final long upStationId, final long downStationId, final int distance
    ) {
        LineSaveRequest lineRequest = new LineSaveRequest(name, color, upStationId, downStationId, distance);
        return RestAssured
                .given()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().extract()
                .as(LineResponse.class);
    }

    protected ExtractableResponse<Response> createLineAndReturnResponse(
            final String name, final String color, final long upStationId, final long downStationId, final int distance
    ) {
        LineSaveRequest lineRequest = new LineSaveRequest(name, color, upStationId, downStationId, distance);
        return RestAssured
                .given()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .extract();
    }
}
