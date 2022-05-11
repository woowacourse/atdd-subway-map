package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class ResisterSectionException extends SubwayException {

    public ResisterSectionException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
