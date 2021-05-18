package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class RestAssuredHelper {
    private RestAssuredHelper() {}

    public static ExtractableResponse<Response> jsonGet(String path) {
        return RestAssured.given().log().all()
                          .when().get(path)
                          .then().log().all()
                          .extract();
    }

    public static ExtractableResponse<Response> jsonPost(Map<String, String> params, String path) {
        return RestAssured.given().log().all().body(params).contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post(path)
                          .then().log().all().extract();
    }

    public static ExtractableResponse<Response> jsonPut(Map<String, String> params, String path) {
        return RestAssured.given().log().all().body(params).contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().put(path)
                          .then().log().all()
                          .extract();
    }

    public static ExtractableResponse<Response> jsonDelete(String path) {
        return RestAssured.given().log().all()
                          .when().delete(path)
                          .then().log().all()
                          .extract();
    }
}
