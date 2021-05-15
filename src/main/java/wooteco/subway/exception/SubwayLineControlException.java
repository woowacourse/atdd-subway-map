package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public abstract class SubwayLineControlException extends RuntimeException {

    private final HttpStatus status;

    public SubwayLineControlException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
