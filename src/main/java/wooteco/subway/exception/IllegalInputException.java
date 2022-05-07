package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class IllegalInputException extends SubwayMapException {

    public IllegalInputException(final String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
