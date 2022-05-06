package wooteco.subway.test_utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;

public class HttpRequestMessage {

    private final RequestSpecification requestSpec;

    private HttpRequestMessage(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    public static HttpRequestMessage of() {
        RequestSpecification requestSpec = RestAssured.given().log().all();
        return new HttpRequestMessage(requestSpec);
    }

    public static HttpRequestMessage ofJsonBody(Object body) {
        RequestSpecification requestSpec = RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        return new HttpRequestMessage(requestSpec);
    }

    public ExtractableResponse<Response> send(HttpMethod method, String path) {
        return method.send(requestSpec, path)
                .then()
                .log().all()
                .extract();
    }
}
