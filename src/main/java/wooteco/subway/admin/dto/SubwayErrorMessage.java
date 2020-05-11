package wooteco.subway.admin.dto;

public class SubwayErrorMessage {
    private final String message;

    public SubwayErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
