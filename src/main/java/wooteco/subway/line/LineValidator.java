package wooteco.subway.line;


import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import wooteco.subway.line.dto.LineRequest;

import java.util.regex.Pattern;

public class LineValidator implements Validator {
    private static final Pattern LINE_NAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]*선$");

    @Override
    public boolean supports(Class<?> clazz) {
        return LineRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LineRequest lineRequest = (LineRequest) target;
        String name = lineRequest.getName();
        if (!LINE_NAME_PATTERN.matcher(name).matches()) {
            errors.rejectValue("name", "invalidNameInput", "지하철 노선 이름이 잘못되었습니다.");
        }
    }
}
