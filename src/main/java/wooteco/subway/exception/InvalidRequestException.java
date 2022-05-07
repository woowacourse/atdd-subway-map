package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends ClientRuntimeException {

    public InvalidRequestException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
