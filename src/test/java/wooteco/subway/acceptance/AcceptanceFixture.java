package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

public class AcceptanceFixture {

    public static final Map<String, String> 강남역 = new HashMap<>() {
        {
            put("name", "강남역");
        }
    };

    public static final Map<String, String> 양재역 = new HashMap<>() {
        {
            put("name", "양재역");
        }
    };

    public static final Map<String, String> 청계산입구역 = new HashMap<>() {
        {
            put("name", "청계산입구역");
        }
    };

    public static final Map<String, String> 일산역 = new HashMap<>() {
        {
            put("name", "일산역");
        }
    };

    public static final Map<String, String> 백석역 = new HashMap<>() {
        {
            put("name", "백석역");
        }
    };

    public static final Map<String, String> 신분당선 = new HashMap<>() {
        {
            put("name", "신분당선");
            put("color", "빨강이");
            put("upStationId", "1");
            put("downStationId", "2");
            put("distance", "10");
        }
    };

    public static final String STATION_URL = "/stations";
    public static final String LINE_URL = "/lines";

    public static ExtractableResponse<Response> getMethodRequest(String path) {
        return RestAssured.given().log().all()
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> postMethodRequest(Map<String, String> parameter, String path) {
        return RestAssured.given().log().all()
                .body(parameter)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> putMethodRequest(Map<String, String> parameter, String path) {
        return RestAssured.given().log().all()
                .body(parameter)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteMethodRequest(String path) {
        return RestAssured.given().log().all()
                .when()
                .delete(path)
                .then().log().all()
                .extract();
    }

    public static List<Long> getExpectedLineIds(List<ExtractableResponse<Response>> createdResponses) {
        return createdResponses.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }

    public static List<Long> getResultLineIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }
}
