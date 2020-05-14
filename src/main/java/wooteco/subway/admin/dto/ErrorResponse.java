package wooteco.subway.admin.dto;

public class ErrorResponse {
    private String errorMessage;

    private ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static ErrorResponse of(String errorMessage) {
        return new ErrorResponse(errorMessage);
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
