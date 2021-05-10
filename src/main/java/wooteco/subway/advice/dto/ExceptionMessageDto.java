package wooteco.subway.advice.dto;

public class ExceptionMessageDto {

    private final String message;

    public ExceptionMessageDto(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
