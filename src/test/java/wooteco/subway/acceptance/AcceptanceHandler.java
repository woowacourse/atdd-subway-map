package wooteco.subway.acceptance;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.controller.dto.line.LineResponse;

class AcceptanceHandler {

    private final String prefixUrl;

    public AcceptanceHandler(String prefixUrl) {
        this.prefixUrl = prefixUrl;
    }

    public ExtractableResponse<Response> save(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(prefixUrl)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> findOne(Long id) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(concatId(id))
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> findAll() {
        return RestAssured.given().log().all()
                .when()
                .get(prefixUrl)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> update(Long id, Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(concatId(id))
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> remove(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(concatId(id))
                .then().log().all()
                .extract();
    }

    public Long extractId(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getObject(".", LineResponse.class)
                .getId();
    }

    public List<Long> extractIds(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getList(".", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toUnmodifiableList());
    }

    private String concatId(Long id) {
        return prefixUrl + "/" + id;
    }
}
