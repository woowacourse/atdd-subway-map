package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

public class SimpleRestAssured {

    public static ExtractableResponse<Response> get(String path) {
        return RestAssured.given().log().all()
            .when()
            .get(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> post(Map<String, Object> params, String path) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> put(Map<String, String> params, String path) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> delete(String path) {
        return RestAssured.given().log().all()
            .when()
            .delete(path)
            .then().log().all()
            .extract();
    }

    public static HashMap<String, Object> makeLineJson(String name, String color, Long upStationId,
        Long downStationId, int distance) {
        return new HashMap<>() {{
            put("name", name);
            put("color", color);
            put("upStationId", upStationId);
            put("downStationId", downStationId);
            put("distance", distance);
        }};
    }

    public static HashMap<String, String> makeLineUpdateJson(String name, String color) {
        return new HashMap<>() {{
            put("name", name);
            put("color", color);
        }};
    }

    public static HashMap<String, Object> makeStationJson(String name) {
        return new HashMap<>() {{
            put("name", name);
        }};
    }

    public static HashMap<String, Object> makeSectionJson(Long upStationId, Long downStationId,
        int distance) {
        return new HashMap<>() {{
            put("upStationId", upStationId);
            put("downStationId", downStationId);
            put("distance", distance);
        }};
    }

    public static void postLine(Map<String, Object> params) {
        post(params, "/lines");
    }

    public static void postStation(Map<String, Object> params) {
        post(params, "/stations");
    }
}
