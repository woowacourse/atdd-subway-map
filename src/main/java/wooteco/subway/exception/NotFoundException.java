package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends SubwayLineControlException {

    public NotFoundException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
