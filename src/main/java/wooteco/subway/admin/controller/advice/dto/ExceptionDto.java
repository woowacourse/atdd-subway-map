package wooteco.subway.admin.controller.advice.dto;

public class ExceptionDto {
    private String message;

    public ExceptionDto() {}

    public ExceptionDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
