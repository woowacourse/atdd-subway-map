package wooteco.subway.admin.controller.advice.dto;

import org.springframework.validation.ObjectError;

import java.util.Objects;

public class MethodArgumentExceptionDto {
    private static final int FIELD_INDEX = 1;
    private static final String TOKEN = "\\.";
    private String field;
    private String message;

    public MethodArgumentExceptionDto(final String field, final String message) {
        this.field = field;
        this.message = message;
    }

    public static MethodArgumentExceptionDto of(ObjectError objectError) {
        String codes = Objects.requireNonNull(objectError.getCodes())[FIELD_INDEX];
        return new MethodArgumentExceptionDto(codes.split(TOKEN)[FIELD_INDEX], objectError.getDefaultMessage());
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
