package wooteco.subway.test_utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import org.springframework.http.MediaType;

public class HttpUtils {

    private HttpUtils() {
    }

    public static ExtractableResponse<Response> send(HttpMethod method, String path) {
        RequestSpecification requestSpec = RestAssured.given().log().all();
        Response response = method.send(requestSpec, path);
        return logAndExtractResponse(response);
    }

    public static ExtractableResponse<Response> send(HttpMethod method, String path, Map<?, ?> body) {
        RequestSpecification requestSpec = RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        Response response = method.send(requestSpec, path);
        return logAndExtractResponse(response);
    }

    private static ExtractableResponse<Response> logAndExtractResponse(Response response) {
        return response.then()
                .log().all()
                .extract();
    }
}
