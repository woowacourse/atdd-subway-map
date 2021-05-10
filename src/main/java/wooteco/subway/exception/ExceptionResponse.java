package wooteco.subway.exception;

public class ExceptionResponse {
    private String message;
    private String cause;

    public ExceptionResponse(String cause, String message) {
        this.cause = cause;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getCause() {
        return cause;
    }
}
