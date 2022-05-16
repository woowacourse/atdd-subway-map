package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.http.MediaType;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.dto.response.StationResponse;

public class AcceptanceTestUtil {
    public static ExtractableResponse<Response> requestPostStation(final StationRequest stationRequest,
                                                                   final String URI) {
        return RestAssured.given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(URI)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestGetStations(final String URI) {
        return RestAssured.given().log().all()
            .when()
            .get(URI)
            .then().log().all()
            .extract();
    }

    public static List<Long> getResultStationIds(final ExtractableResponse<Response> response) {
        return response.jsonPath()
            .getList(".", StationResponse.class)
            .stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
    }

    public static List<Long> getExpectedStationIds(final ExtractableResponse<Response> createResponse1,
                                                   final ExtractableResponse<Response> createResponse2) {
        return Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
    }

    public static ExtractableResponse<Response> requestDeleteStation(
        final ExtractableResponse<Response> createResponse) {
        return RestAssured.given().log().all()
            .when()
            .delete(createResponse.header("Location"))
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestPostLine(final LineRequest lineRequest, final String URI) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(URI)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestGetLines(final String URI) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(URI)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestPutLine(final LineRequest lineRequest, final String URI) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(URI)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestDeleteLine(final String URI) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete(URI)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestPostSection(final SectionRequest sectionRequest,
                                                                   final String URI) {
        return RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(URI)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestDeleteSection(final String URI) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete(URI)
            .then().log().all()
            .extract();
    }
}
