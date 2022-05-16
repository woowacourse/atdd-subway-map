package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.StationRequest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    @LocalServerPort
    int port;

    protected static final StationRequest 대흥역 = new StationRequest("대흥역");
    protected static final StationRequest 공덕역 = new StationRequest("공덕역");

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    protected Long postStationId(StationRequest stationRequest) {
        return Long.valueOf(postStationResponse(stationRequest)
                .header("Location")
                .split("/")[2]);
    }

    protected ExtractableResponse<Response> postStationResponse(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> postLineResponse(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    protected List<Long> getExpectedLineIds(ExtractableResponse<Response> response1,
                                            ExtractableResponse<Response> response2) {
        return Stream.of(response1, response2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }
}
