package wooteco.subway.common;

public class ExceptionResponse {
    final String message;

    public ExceptionResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
