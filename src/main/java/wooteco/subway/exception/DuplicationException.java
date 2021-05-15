package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class DuplicationException extends SubwayLineControlException {

    public DuplicationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
