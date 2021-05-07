package wooteco.subway.exception;

public class ErrorResponse {

    private final String errorType;
    private final String message;

    private ErrorResponse(String errorType, String message) {
        this.errorType = errorType;
        this.message = message;
    }

    public static ErrorResponse of(String errorType, String message) {
        return new ErrorResponse(errorType, message);
    }
}
