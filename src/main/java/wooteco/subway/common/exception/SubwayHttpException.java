package wooteco.subway.common.exception;

import org.springframework.http.HttpStatus;

public class SubwayHttpException extends RuntimeException implements HttpException {

    private static final String ERROR_MESSAGE = "오류가 발생했습니다";

    private final HttpStatus status;
    private final Object body;

    public SubwayHttpException(HttpStatus status, Object body) {
        super(ERROR_MESSAGE);
        this.status = status;
        this.body = body;
    }

    @Override
    public HttpStatus httpStatus() {
        return status;
    }

    @Override
    public Object body() {
        return body;
    }
}
