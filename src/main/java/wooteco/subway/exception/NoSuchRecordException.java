package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class NoSuchRecordException extends SubwayMapException {

    public NoSuchRecordException(final String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
