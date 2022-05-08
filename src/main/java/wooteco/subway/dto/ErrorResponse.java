package wooteco.subway.dto;

public class ErrorResponse {

    private String message;

    private ErrorResponse() {
    }

    private ErrorResponse(final String message) {
        this.message = message;
    }

    public static ErrorResponse from(final Exception e) {
        return new ErrorResponse(e.getMessage());
    }

    public static ErrorResponse from(final String message) {
        return new ErrorResponse(message);
    }

    public String getMessage() {
        return message;
    }
}
