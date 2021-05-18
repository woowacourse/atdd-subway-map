package wooteco.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.station.dto.StationRequest;

@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    @LocalServerPort
    int port;

    protected ExtractableResponse<Response> 강남역_response;
    protected ExtractableResponse<Response> 역삼역_response;
    protected ExtractableResponse<Response> 도곡역_response;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        강남역_response = postStation("강남역");
        역삼역_response = postStation("역삼역");
        도곡역_response = postStation("도곡역");
    }

    protected ExtractableResponse<Response> getStations() {
        return RestAssured.given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> postStation(String name) {
        return RestAssured.given().log().all()
            .body(new StationRequest(name))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> deleteStation(Long stationId) {
        return RestAssured.given().log().all()
            .when()
            .delete("/stations/{id}", stationId)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> getLines() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> getLine(Long lineId) {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/{id}", lineId)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> postLine(LineRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    protected void postSection(SectionRequest request, Long lineId) {
        RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/{id}/sections", lineId)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> deleteSection(Long stationId, Long lineId) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .queryParam("stationId", stationId)
            .when()
            .delete("/lines/{id}/sections", lineId)
            .then().log().all()
            .extract();
    }
}
