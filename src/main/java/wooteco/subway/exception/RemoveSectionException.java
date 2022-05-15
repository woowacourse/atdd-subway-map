package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class RemoveSectionException extends SubwayException {

    public RemoveSectionException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
