package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class NoSuchContentException extends SubwayException {

    public NoSuchContentException(String message, HttpStatus status) {
        super(message, status);
    }
}
