package wooteco.subway.acceptance.request;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineRequest {
    public static ExtractableResponse<Response> createLineRequest(Map<String, String> params) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        return response;
    }

    public static Map<String, String> line1(Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "1호선");
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", "10");
        return params;
    }

    public static Map<String, String> line2(Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-green-600");
        params.put("name", "2호선");
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", "5");
        return params;
    }

    public static ExtractableResponse<Response> createSectionRequest(Map<String, String> params, Long lineId) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();
        return response;
    }

    public static Map<String, String> section1(Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("downStationId", downStationId.toString());
        params.put("upStationId", upStationId.toString());
        params.put("distance", "10");
        return params;
    }
}
