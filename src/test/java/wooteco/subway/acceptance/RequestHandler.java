package wooteco.subway.acceptance;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class RequestHandler {

    public ExtractableResponse<Response> getRequest(String url) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(url)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> postRequest(String url, Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(url)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> putRequest(String url, Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(url)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> deleteRequest(String url) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(url)
                .then().log().all()
                .extract();
    }

    public <T> T extractId(ExtractableResponse<Response> response, Class<T> responseClass) {
        return response.jsonPath()
                .getObject(".", responseClass);
    }

    public <T> List<T> extractIds(ExtractableResponse<Response> response, Class<T> responseClass) {
        return response.jsonPath()
                .getList(".", responseClass)
                .stream()
                .collect(Collectors.toUnmodifiableList());
    }
}
