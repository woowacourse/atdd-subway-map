package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class DuplicatedNameException extends RuntimeException {

    public DuplicatedNameException(final String message) {
        super(message);
    }
}
