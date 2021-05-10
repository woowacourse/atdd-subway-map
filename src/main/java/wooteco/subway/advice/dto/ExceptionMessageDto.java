package wooteco.subway.advice.dto;

public class ExceptionMessageDto {

    private String message;

    public ExceptionMessageDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
