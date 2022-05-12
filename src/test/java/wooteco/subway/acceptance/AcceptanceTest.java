package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.StationRequest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    protected long createStationAndReturnId(final String stationName) {

        return createStationAndReturnResponse(stationName)
                .body().jsonPath().getLong("id");
    }

    protected ExtractableResponse<Response> createStationAndReturnResponse(final String stationName) {
        return RestAssured.given()
                .body(new StationRequest(stationName))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().extract();
    }

    protected ExtractableResponse<Response> createLineAndReturnResponse(
            final String name, final String color, final long upStationId, final long downStationId, final int distance
    ) {
        LineRequest lineRequestV2 = new LineRequest(name, color, upStationId, downStationId, distance);
        return RestAssured
                .given()
                .body(lineRequestV2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .extract();
    }
}
