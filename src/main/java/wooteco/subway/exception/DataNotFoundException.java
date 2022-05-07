package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class DataNotFoundException extends ClientRuntimeException {

    public DataNotFoundException(final String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
