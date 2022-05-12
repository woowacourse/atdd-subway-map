package wooteco.subway.acceptance.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static wooteco.subway.acceptance.utils.StationAcceptanceTestFixture.역_생성_요청;

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

    public static ExtractableResponse<Response> 노선_목록_조회_요청() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
        return response;
    }


    public static ExtractableResponse<Response> 노선_조회_요청(Long createdId) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + createdId)
                .then().log().all()
                .extract();
    }

    public static ValidatableResponse 노선_수정_요청(long createdId, String name, String color) {
        return RestAssured.given().log().all()
                .body(Map.of("name", name, "color", color))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + createdId)
                .then().log().all();
    }

    public static ValidatableResponse 노선_삭제_요청(long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + lineId)
                .then().log().all();
    }

    public static ValidatableResponse 노선_및_역들_생성요청_케이스_1번() {
        ExtractableResponse<Response> createStationResponse1 = 역_생성_요청("노량진역");
        long stationId1 = createStationResponse1.jsonPath().getLong("id");

        ExtractableResponse<Response> createStationResponse2 = 역_생성_요청("영등포역");
        long stationId2 = createStationResponse2.jsonPath().getLong("id");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "1호선");
        requestBody.put("color", "blue");
        requestBody.put("upStationId", String.valueOf(stationId1));
        requestBody.put("downStationId", String.valueOf(stationId2));
        requestBody.put("distance", "10");

        return 노선_생성_요청(requestBody);
    }

    public static ValidatableResponse 노선_및_역들_생성요청_케이스_2번() {
        ExtractableResponse<Response> createStationResponse1 = 역_생성_요청("강남구청역");
        long stationId1 = createStationResponse1.jsonPath().getLong("id");

        ExtractableResponse<Response> createStationResponse2 = 역_생성_요청("선릉역");
        long stationId2 = createStationResponse2.jsonPath().getLong("id");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "신분당선");
        requestBody.put("color", "yellow");
        requestBody.put("upStationId", String.valueOf(stationId1));
        requestBody.put("downStationId", String.valueOf(stationId2));
        requestBody.put("distance", "10");

        return 노선_생성_요청(requestBody);
    }
}
