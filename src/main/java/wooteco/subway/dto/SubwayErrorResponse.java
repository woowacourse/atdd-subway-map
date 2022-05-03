package wooteco.subway.dto;

public class SubwayErrorResponse {

    private String message;

    private SubwayErrorResponse() {
    }

    private SubwayErrorResponse(final String message) {
        this.message = message;
    }

    public static SubwayErrorResponse from(RuntimeException exception) {
        return new SubwayErrorResponse(exception.getMessage());
    }

    public String getMessage() {
        return message;
    }
}
