package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class RequestException extends RuntimeException {

    private static final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public RequestException(String message) {
        super(message);
    }

    public int status() {
        return httpStatus.value();
    }
}
