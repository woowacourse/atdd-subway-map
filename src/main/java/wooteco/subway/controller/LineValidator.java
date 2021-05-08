package wooteco.subway.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.exception.line.LineNameFormatException;
import wooteco.subway.exception.line.LineNameNullException;

import java.util.regex.Pattern;

public class LineValidator implements Validator {

    private static final Pattern PATTERN = Pattern.compile("^[가-힣|0-9]*선$");

    @Override
    public boolean supports(Class<?> clazz) {
        return LineRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LineRequest lineRequest = (LineRequest) target;
        String name = lineRequest.getName();

        if (name == null) {
            throw new LineNameNullException();
        }

        if (!PATTERN.matcher(name).matches()) {
            throw new LineNameFormatException();
        }
    }
}
