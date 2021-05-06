package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class WebException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final Object body;

    public WebException(HttpStatus httpStatus, Object body) {
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Object getBody() {
        return body;
    }
}
