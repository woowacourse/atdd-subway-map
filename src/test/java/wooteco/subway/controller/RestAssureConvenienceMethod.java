package wooteco.subway.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class RestAssureConvenienceMethod {

    public static ExtractableResponse<Response>  postRequest(Object body, String mediaType, String path) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(mediaType)
                .when()
                .post(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response>  deleteRequest(String path) {
        return RestAssured.given().log().all()
                .when()
                .delete(path)
                .then().log().all()
                .extract();
    }
}
