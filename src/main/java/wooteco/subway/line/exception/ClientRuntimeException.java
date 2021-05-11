package wooteco.subway.line.exception;

import org.springframework.http.HttpStatus;

public class ClientRuntimeException extends RuntimeException {

    public ClientRuntimeException(final String message) {
        super(message);
    }
}
