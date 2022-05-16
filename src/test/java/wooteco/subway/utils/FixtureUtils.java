package wooteco.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.StationRequest;

import java.util.List;
import java.util.Map;

import static java.util.Collections.EMPTY_MAP;

@SuppressWarnings("NonAsciiCharacters")
public class FixtureUtils {

    public static final String LINE = "/lines";
    public static final String STATION = "/stations";
    public static final StationRequest 상도역 = StationRequest.builder()
            .name("상도역")
            .build();

    public static final StationRequest 이수역 = StationRequest.builder()
            .name("이수역")
            .build();

    public static final StationRequest 강남구청역 = StationRequest.builder()
            .name("강남구청역")
            .build();

    public static final StationRequest 선릉역 = StationRequest.builder()
            .name("선릉역")
            .build();

    public static LineRequest 신분당선 = LineRequest.builder()
            .name("신분당선")
            .color("yellow")
            .upStationId(1L)
            .downStationId(2L)
            .distance(7)
            .build();

    public static LineRequest _7호선 = LineRequest.builder()
            .name("7호선")
            .color("brown")
            .upStationId(1L)
            .downStationId(2L)
            .distance(7)
            .build();

    private FixtureUtils() {
    }

    public static ExtractableResponse<Response> get(String path, Map<String, String> requestBody) {
        return postProcess(preProcess(requestBody).get(path));
    }

    public static ExtractableResponse<Response> get(String path) {
        return get(path, EMPTY_MAP);
    }

    public static ExtractableResponse<Response> put(String path, Object requestBody) {
        return postProcess(preProcess(requestBody).put(path));
    }

    public static ExtractableResponse<Response> post(String path, Object requestBody) {
        return postProcess(preProcess(requestBody).post(path));
    }

    public static ExtractableResponse<Response> delete(String path, Object requestBody) {
        return postProcess(preProcess(requestBody).delete(path));
    }

    public static ExtractableResponse<Response> delete(String path) {
        return postProcess(preProcess(EMPTY_MAP).delete(path));
    }

    public static <T> T convertType(ExtractableResponse<Response> response, Class<T> clazz) {
        return response.jsonPath().getObject(".", clazz);
    }

    public static <T> List<T> convertTypeList(ExtractableResponse<Response> response, Class<T> clazz) {
        return response.jsonPath().getList(".", clazz);
    }

    public static long extractId(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject("id", Long.class);
    }

    public static String lineById(Object id) {
        return LINE + "/" + id;
    }

    public static String stationById(Object id) {
        return STATION + "/" + id;
    }

    public static LineRequest 신분당선_생성() {
        return LineRequest.builder()
                .name("신분당선")
                .color("yellow")
                .distance(7)
                .build();
    }

    public static LineRequest _7호선_생성() {
        return LineRequest.builder()
                .name("7호선")
                .color("brown")
                .distance(7)
                .build();
    }

    private static RequestSpecification preProcess(Object requestBody) {
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
