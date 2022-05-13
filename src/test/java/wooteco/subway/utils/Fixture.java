package wooteco.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;

import java.util.Map;

public class Fixture {

    public static String LINE = "/lines";
    public static String STATION = "/stations";
    public static Map<String, String> 상도역 = Map.of("name", "상도역");
    public static Map<String, String> 이수역 = Map.of("name", "이수역");

    public static Map<String, String> 신분당선 = Map.of(
            "name", "신분당선",
            "color", "yellow",
            "upStationId", "1",
            "downStationId", "2",
            "distance", "7"
    );
    public static Map<String, String> _1호선 = Map.of(
            "name", "1호선",
            "color", "blue",
            "upStationId", "1",
            "downStationId", "2",
            "distance", "7"
    );

    private Fixture() {
    }

    public static ExtractableResponse<Response> get(String path, Map<String, String> requestBody) {
        return postProcess(preProcess(requestBody).get(path));
    }

    public static ExtractableResponse<Response> put(String path, Map<String, String> requestBody) {
        return postProcess(preProcess(requestBody).put(path));
    }

    public static ExtractableResponse<Response> post(String path, Map<String, String> requestBody) {
        return postProcess(preProcess(requestBody).post(path));
    }

    public static ExtractableResponse<Response> delete(String path, Map<String, String> requestBody) {
        return postProcess(preProcess(requestBody).delete(path));
    }

    private static RequestSpecification preProcess(Map<String, String> requestBody) {
        return RestAssured.given().log().all()
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when();
    }

    private static ExtractableResponse<Response> postProcess(Response response) {
        return response
                .then().log().all().extract();
    }
}
