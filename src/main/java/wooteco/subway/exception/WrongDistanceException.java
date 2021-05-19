package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class WrongDistanceException extends SubwayLineControlException {

    public WrongDistanceException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
