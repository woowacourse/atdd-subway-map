package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;

public class TestFixtures {

    public static <T> ExtractableResponse<Response> extractPostResponse(T request, String url) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(url)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> extractGetResponse(String uri) {
        return RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> extractDeleteResponse(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> extractPutResponse(LineRequest updateLineRequest, String uri) {
        return RestAssured.given().log().all()
                .body(updateLineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();
    }
}
