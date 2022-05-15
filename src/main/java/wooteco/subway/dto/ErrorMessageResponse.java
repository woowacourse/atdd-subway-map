package wooteco.subway.dto;

public class ErrorMessageResponse {

    private final String message;

    public ErrorMessageResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
