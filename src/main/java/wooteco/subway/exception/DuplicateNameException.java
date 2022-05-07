package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class DuplicateNameException extends ClientRuntimeException {

    public DuplicateNameException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
