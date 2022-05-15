package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class NoElementSectionsException extends SubwayException {

    public NoElementSectionsException(final String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
