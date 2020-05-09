package wooteco.subway.admin.common.advice.dto;

public class UnprocessableExceptionDto {
    private String message;

    protected UnprocessableExceptionDto() {
    }

    public UnprocessableExceptionDto(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
