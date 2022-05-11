package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class IllegalDeleteException extends ClientRuntimeException {

    public IllegalDeleteException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
