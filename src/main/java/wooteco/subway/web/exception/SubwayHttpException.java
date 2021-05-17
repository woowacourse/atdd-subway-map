package wooteco.subway.web.exception;

import org.springframework.http.HttpStatus;

public class SubwayHttpException extends SubwayException implements HttpException {

    private final HttpStatus status;
    private final String body;

    public SubwayHttpException(String body) {
        this(HttpStatus.BAD_REQUEST, body);
    }

    public SubwayHttpException(HttpStatus status, String body) {
        super(body);
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
