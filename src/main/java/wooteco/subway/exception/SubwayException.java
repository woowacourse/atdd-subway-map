package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

import javax.security.auth.Subject;

public class SubwayException extends RuntimeException {
    private final HttpStatus status;

    public SubwayException(final String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public SubwayException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
