package wooteco.subway.ui.dto;

public class ExceptionResponse {

    private boolean ok;
    private String message;

    private ExceptionResponse(String message) {
        this.ok = false;
        this.message = message;
    }

    public ExceptionResponse() {
    }

    public static ExceptionResponse from(String message) {
        return new ExceptionResponse(message);
    }

    public boolean isOk() {
        return ok;
    }

    public String getMessage() {
        return message;
    }
}
