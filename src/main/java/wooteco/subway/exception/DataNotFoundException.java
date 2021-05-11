package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class DataNotFoundException extends RuntimeException {

    public DataNotFoundException(final String message) {
        super(message);
    }
}
