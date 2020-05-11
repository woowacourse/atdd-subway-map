package wooteco.subway.admin.common.advice.dto;

import org.springframework.validation.ObjectError;

import java.util.Objects;

public class ArgumentExceptionDto {
    private static final int FIELD_INDEX = 1;
    private static final String TOKEN = "\\.";
    private String field;
    private String message;

    protected ArgumentExceptionDto() {
    }

    public ArgumentExceptionDto(final String field, final String message) {
        this.field = field;
        this.message = message;
    }

    public static ArgumentExceptionDto of(ObjectError objectError) {
        String codes = Objects.requireNonNull(objectError.getCodes())[FIELD_INDEX];
        return new ArgumentExceptionDto(codes.split(TOKEN)[FIELD_INDEX], objectError.getDefaultMessage());
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
