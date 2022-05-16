package wooteco.subway.acceptance;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

import wooteco.subway.dto.LineRequest;

public class AcceptanceFixture {

    private AcceptanceFixture() {
    }

    public static <T> ValidatableResponse insert(T request, String path, int statusCode) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all()
                .statusCode(statusCode);
    }

    public static ValidatableResponse select(String path, int statusCode) {
        return RestAssured.given().log().all()
                .when()
                .get(path)
                .then().log().all()
                .statusCode(statusCode);
    }

    public static ValidatableResponse put(String path, LineRequest request, int statusCode) {
        return RestAssured.given().log()
                .all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(path)
                .then().log().all()
                .statusCode(statusCode);
    }

    public static ValidatableResponse delete(String path, int statusCode) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(path)
                .then().log().all()
                .statusCode(statusCode);
    }
}
