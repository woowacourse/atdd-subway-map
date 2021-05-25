package wooteco.subway.exception.badrequest;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class BadRequest extends SubwayException {
    private static final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;

    public BadRequest(String message) {
        super(message, BAD_REQUEST);
    }

    public BadRequest(String message, Throwable cause) {
        super(message, cause, BAD_REQUEST);
    }
}
