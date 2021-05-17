package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class RequestException extends BusinessException {

    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public RequestException(String message) {
        super(message);
    }

    public int status() {
        return HTTP_STATUS.value();
    }
}
