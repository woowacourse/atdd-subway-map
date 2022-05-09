package wooteco.subway.test_utils;

import io.restassured.response.Response;
import io.restassured.specification.RequestSenderOptions;
import io.restassured.specification.RequestSpecification;
import java.util.function.BiFunction;

public enum HttpMethod {

    GET(RequestSenderOptions::get),
    POST(RequestSpecification::post),
    PUT(RequestSpecification::put),
    DELETE(RequestSpecification::delete),
    ;

    private final BiFunction<RequestSpecification, String, Response> requestSender;

    HttpMethod(BiFunction<RequestSpecification, String, Response> requestSender) {
        this.requestSender = requestSender;
    }

    Response send(RequestSpecification requestSpec, String path) {
        return requestSender.apply(requestSpec, path);
    }
}
