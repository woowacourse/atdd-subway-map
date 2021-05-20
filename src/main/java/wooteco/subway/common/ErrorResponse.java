package wooteco.subway.common;

public class ErrorResponse {
    final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
