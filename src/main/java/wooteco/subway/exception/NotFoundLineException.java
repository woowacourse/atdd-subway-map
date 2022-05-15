package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class NotFoundLineException extends SubwayException {

    public NotFoundLineException(final String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
