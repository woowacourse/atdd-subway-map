package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class SubwayException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    public SubwayException(String message) {
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
