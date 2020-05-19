package wooteco.subway.admin.dto;

public class ErrorResponse {
    private int status;
    private String message;

    public ErrorResponse() {
    }

    private ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message);
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
