package wooteco.subway.admin.exception;

import org.springframework.http.HttpStatus;

public class CommonException extends RuntimeException{
    private HttpStatus httpStatus;

    public CommonException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
