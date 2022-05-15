package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class DuplicateNameException extends SubwayException {

    public DuplicateNameException(final String errorMessage) {
        super(HttpStatus.BAD_REQUEST, errorMessage);
    }
}
