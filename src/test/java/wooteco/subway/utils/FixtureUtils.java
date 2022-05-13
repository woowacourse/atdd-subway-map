package wooteco.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;

import java.util.Map;

import static java.util.Collections.EMPTY_MAP;

@SuppressWarnings("NonAsciiCharacters")
public class FixtureUtils {

    public static String LINE = "/lines";
    public static String STATION = "/stations";
    public static Map<String, String> 상도역 = Map.of("name", "상도역");
    public static Map<String, String> 이수역 = Map.of("name", "이수역");
    public static Map<String, String> 선릉역 = Map.of("name", "선릉역");
    public static Map<String, String> 강남구청역 = Map.of("name", "강남구청역");

    public static Map<String, String> 신분당선 = Map.of(
            "name", "신분당선",
            "color", "yellow",
            "upStationId", "1",
            "downStationId", "2",
            "distance", "7"
    );
    public static Map<String, String> _7호선 = Map.of(
            "name", "7호선",
            "color", "brown",
            "upStationId", "1",
            "downStationId", "2",
            "distance", "7"
    );

    private FixtureUtils() {
    }

    public static ExtractableResponse<Response> get(String path, Map<String, String> requestBody) {
        return postProcess(preProcess(requestBody).get(path));
    }

    public static ExtractableResponse<Response> get(String path) {
        return get(path, EMPTY_MAP);
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

    public static ExtractableResponse<Response> delete(String path) {
        return postProcess(preProcess(EMPTY_MAP).delete(path));
    }

    public static <T> T convertObject(ExtractableResponse<Response> response, Class<T> clazz) {
        return response.jsonPath().getObject(".", clazz);
    }

    public static long extractId(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject("id", Long.class);
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
