package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class DuplicatedNameException extends ClientRuntimeException {

    public DuplicatedNameException(final String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
