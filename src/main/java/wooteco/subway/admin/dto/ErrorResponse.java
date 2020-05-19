package wooteco.subway.admin.dto;

public class ErrorResponse {
    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public static ErrorResponse of(String error) {
        return new ErrorResponse(error);
    }

    public String getMessage() {
        return message;
    }
}
