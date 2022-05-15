package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

public class AcceptanceUtil {

    public static Long createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        ExtractableResponse<Response> response = AcceptanceUtil.postRequest(params, "/stations");
        return Long.parseLong(response.header("Location").split("stations/")[1]);
    }

    public static Long createLine(String name, String color, Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", distance.toString());

        ExtractableResponse<Response> response = AcceptanceUtil.postRequest(params, "/lines");
        return Long.parseLong(response.header("Location").split("lines/")[1]);
    }

    public static ExtractableResponse<Response> postRequest(Map<String, String> params, String path) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all()
                .extract();
    }
}
