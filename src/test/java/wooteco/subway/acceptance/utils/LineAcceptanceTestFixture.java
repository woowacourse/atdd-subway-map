package wooteco.subway.acceptance.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineAcceptanceTestFixture {

    private LineAcceptanceTestFixture() {
    }

    public static ExtractableResponse<Response> 노선_생성_요청(String lineName, String lineColor) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", lineName);
        requestBody.put("color", lineColor);

        return 노선_생성_요청(requestBody)
                .extract();
    }

    public static ValidatableResponse 노선_생성_요청(Map<String, String> requestBody) {
        return RestAssured.given().log().all()
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all();
    }

    public static  ExtractableResponse<Response> 노선_목록_조회_요청(Long createdId) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + createdId)
                .then().log().all()
                .extract();
    }

    public static  ValidatableResponse 노선_수정_요청(long createdId, String name, String color) {
        return RestAssured.given().log().all()
                .body(Map.of("name", name, "color", color))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + createdId)
                .then().log().all();
    }

    public static  ValidatableResponse 노선_삭제_요청(long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + lineId)
                .then().log().all();
    }
}
