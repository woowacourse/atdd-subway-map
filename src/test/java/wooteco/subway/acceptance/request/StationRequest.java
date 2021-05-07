package wooteco.subway.acceptance.request;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class StationRequest {
    public static ExtractableResponse<Response> createStationRequest(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static Map<String, String> station1() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        return params;
    }

    public static Map<String, String> station2() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "길동역");
        return params;
    }
}
