package wooteco.subway.admin.dto;

public class ErrorResponse {
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public static ErrorResponse of(String error) {
        return new ErrorResponse(error);
    }

    public String getError() {
        return error;
    }
}
