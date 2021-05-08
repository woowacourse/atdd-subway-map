package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class DataNotFoundException extends ClientRuntimeException {

    public DataNotFoundException(final String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
