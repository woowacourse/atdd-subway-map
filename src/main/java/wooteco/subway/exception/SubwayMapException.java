package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class SubwayMapException extends RuntimeException {

    private final HttpStatus httpStatus;

    public SubwayMapException(final String message, final HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
