package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class DatabaseException extends BusinessException {

    private static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public DatabaseException(String message) {
        super(message);
    }

    public int status() {
        return HTTP_STATUS.value();
    }
}
