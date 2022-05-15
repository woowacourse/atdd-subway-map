package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class NotFoundStationException extends SubwayException {

    public NotFoundStationException(final String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
