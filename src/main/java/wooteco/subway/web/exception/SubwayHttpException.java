package wooteco.subway.web.exception;

import org.springframework.http.HttpStatus;

public class SubwayHttpException extends RuntimeException implements HttpException {

    private static final String ERROR_MESSAGE = "오류가 발생했습니다";

    private final HttpStatus status;
    private final String body;

    public SubwayHttpException() {
        this(ERROR_MESSAGE);
    }

    public SubwayHttpException(String body) {
        this(HttpStatus.BAD_REQUEST, body);
    }

    public SubwayHttpException(HttpStatus status, String body) {
        super(ERROR_MESSAGE);
        this.status = status;
        this.body = body;
    }

    @Override
    public HttpStatus httpStatus() {
        return status;
    }

    @Override
    public String body() {
        return body;
    }
}
