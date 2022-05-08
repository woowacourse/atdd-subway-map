package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class ClientRuntimeException extends RuntimeException {

    private final HttpStatus httpStatus;

    public ClientRuntimeException(final HttpStatus httpStatus, final String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
