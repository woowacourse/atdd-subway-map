package wooteco.subway.admin.dto;

public class ErrorResponse {
    private String errorType;

    public ErrorResponse(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorType() {
        return errorType;
    }
}
