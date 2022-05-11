package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class IllegalSectionInsertException extends ClientRuntimeException {

    public IllegalSectionInsertException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
