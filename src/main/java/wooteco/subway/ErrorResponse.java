package wooteco.subway;

public class ErrorResponse {
    final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
