package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class SectionAdditionException extends SubwayLineControlException {

    public SectionAdditionException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
