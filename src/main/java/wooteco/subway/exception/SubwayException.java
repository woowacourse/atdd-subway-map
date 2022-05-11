package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class SubwayException extends RuntimeException {
    private final HttpStatus status;

    public SubwayException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
