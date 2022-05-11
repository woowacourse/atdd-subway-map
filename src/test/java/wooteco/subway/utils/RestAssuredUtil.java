package wooteco.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.springframework.http.MediaType;

public class RestAssuredUtil {

    private RestAssuredUtil() {
    }

    public static ExtractableResponse<Response> post(String url, Object body) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(url)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> get(String url) {
        return RestAssured.given().log().all()
                .when()
                .get(url)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> delete(String url, Map<String, String> map) {
        return RestAssured.given().log().all()
                .queryParams(map)
                .when()
                .delete(url)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> put(String url, Object object) {
        return RestAssured.given().log().all()
                .body(object)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(url)
                .then().log().all()
                .extract();
    }
}
