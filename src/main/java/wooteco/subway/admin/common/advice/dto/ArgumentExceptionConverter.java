package wooteco.subway.admin.common.advice.dto;

import org.springframework.validation.ObjectError;

import java.util.Objects;

public class ArgumentExceptionConverter {
    private static final int FIELD_INDEX = 1;
    private static final String TOKEN = "\\.";

    public static DefaultExceptionResponse<String> of(ObjectError objectError) {
        String codes = Objects.requireNonNull(objectError.getCodes())[FIELD_INDEX];
        return new DefaultExceptionResponse<>(codes.split(TOKEN)[FIELD_INDEX], objectError.getDefaultMessage());
    }

}
