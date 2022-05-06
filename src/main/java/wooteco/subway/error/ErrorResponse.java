package wooteco.subway.error;

public class ErrorResponse {

    private final String cause;
    private final String message;

    public ErrorResponse(String cause, String message) {
        this.cause = cause;
        this.message = message;
    }

    public String getCause() {
        return cause;
    }

    public String getMessage() {
        return message;
    }
}
