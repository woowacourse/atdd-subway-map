package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import wooteco.subway.controller.dto.line.LineRequest;
import wooteco.subway.controller.dto.line.LineResponse;
import wooteco.subway.controller.dto.section.SectionRequest;
import wooteco.subway.controller.dto.station.StationRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ResponseCreator {

    protected static ExtractableResponse<Response> createPostStationResponse(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    protected static ExtractableResponse<Response> createPostLineResponse(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    protected static ExtractableResponse<Response> createGetLineResponseById(String id) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    protected static ExtractableResponse<Response> createGetLinesResponse() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    protected static List<Long> postIds(ExtractableResponse<Response>... createResponse) {
        return Arrays.asList(createResponse).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }

    protected static List<Long> responseIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    protected static ExtractableResponse<Response> createPutLineResponse(String id, LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    protected static ExtractableResponse<Response> createDeleteLineResponseById(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    protected static ExtractableResponse<Response> createDeleteLineResponseById(String id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    protected static ExtractableResponse<Response> deleteSectionResponse(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when()
                .queryParam("stationId", stationId)
                .delete("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    protected static ExtractableResponse<Response> createPostSectionResponse(Long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    protected static ExtractableResponse<Response> createGetStationResponse() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    protected static ExtractableResponse<Response> createDeleteStationResponseById(String id) {
        return RestAssured.given().log().all()
                .when()
                .delete("/stations/" + id)
                .then().log().all()
                .extract();
    }
}
