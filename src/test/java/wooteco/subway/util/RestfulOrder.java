package wooteco.subway.util;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.springframework.http.MediaType;


public class RestfulOrder {

    public static final String DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON_VALUE;

    public static void testRequest(Map<String, String> params, String mediaType, String path) {
        RestAssured.given().log().all()
            .body(params)
            .contentType(mediaType)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> testResponse(Map<String, String> params,
        String mediaType, String path) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(mediaType)
            .when()
            .post(path)
            .then().log().all()
            .extract();

        return response;
    }
}
