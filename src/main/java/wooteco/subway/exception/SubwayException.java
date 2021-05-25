package wooteco.subway.exception;

import org.springframework.http.HttpStatus;
import wooteco.subway.advice.dto.ExceptionResponse;

public class SubwayException extends RuntimeException {
    private HttpStatus httpStatus;
    private ExceptionResponse body;

    public SubwayException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.body = new ExceptionResponse(message, httpStatus.value());
    }

    public SubwayException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public ExceptionResponse body() {
        return body;
    }
}
