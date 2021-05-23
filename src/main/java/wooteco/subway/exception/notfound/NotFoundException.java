package wooteco.subway.exception.notfound;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class NotFoundException extends SubwayException {
    private static final HttpStatus NOT_FOUND = HttpStatus.NOT_FOUND;

    public NotFoundException(String message) {
        super(message, NOT_FOUND);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause, NOT_FOUND);
    }
}
