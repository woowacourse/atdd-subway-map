package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineRequestForm {

    private LineRequestForm() {
    }

    public static Map<String, String> LineRequestBody(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }

    public static ExtractableResponse<Response> createRequest(Map<String, String> body) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> findAllRequest() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> findByIdRequest(String id) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> updateRequest(Map<String, String> body, String id) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteRequest(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }
}
