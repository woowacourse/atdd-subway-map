package wooteco.subway.acceptance.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class StationAcceptanceTestFixture {

    private StationAcceptanceTestFixture() {
    }

    public static ExtractableResponse<Response> 역_생성_요청(String stationName) {
        return RestAssured.given().log().all()
                .body(Map.of("name", stationName))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 역_삭제_요청(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 역_목록_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }
}
