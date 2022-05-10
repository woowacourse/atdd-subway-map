package wooteco.subway.acceptance.fixture;

import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class SimpleRestAssured {

    public static ExtractableResponse<Response> get(String path) {
        return thenExtract(given()
            .when().get(path));
    }

    public static ExtractableResponse<Response> post(String path, Map<String, String> params) {
        return thenExtract(given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(path));
    }

    public static ExtractableResponse<Response> put(String path, Map<String, String> params) {
        return thenExtract(given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put(path));
    }

    public static ExtractableResponse<Response> delete(String path) {
        return thenExtract(given()
            .when().delete(path));
    }

    private static RequestSpecification given() {
        return RestAssured.given().log().all();
    }

    private static ExtractableResponse<Response> thenExtract(Response response) {
        return response
            .then().log().all()
            .extract();
    }

    public static <T> T toObject(ExtractableResponse<Response> response, Class<T> clazz) {
        return response.body().jsonPath().getObject(".", clazz);
    }

}
