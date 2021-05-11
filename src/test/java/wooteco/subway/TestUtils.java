package wooteco.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.station.controller.dto.StationRequest;

public class TestUtils {

    public static ExtractableResponse<Response> postStation(final StationRequest stationRequest) {
        return RestAssured
                .given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getStations() {
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> deleteStation(final String uri) {
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> postLine(final LineRequest lineRequest) {
        return RestAssured
                .given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getLine(final Long id) {
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .get("/lines/{id}", id)
                .then().log().all()
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> getLines() {
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> updateLine(final String uri, final LineRequest updateRequest) {
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> deleteLine(final String uri) {
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .delete(uri)
                .then()
                .extract();
        return response;
    }
}
