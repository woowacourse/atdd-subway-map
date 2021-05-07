package wooteco.subway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.request.StationRequest;

import java.util.regex.Pattern;

public class LineValidator implements Validator {

    private static final Pattern PATTERN = Pattern.compile("^[가-힣|A-Z|a-z| 0-9]*선$");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean supports(Class<?> clazz) {
        return StationRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LineRequest lineRequest = (LineRequest) target;
        String name = lineRequest.getName();
        if (name == null) {
            errors.rejectValue("name", "required", "노선 이름은 필수로 입력해야합니다.");
        } else if (!PATTERN.matcher(name).matches()) {
            errors.rejectValue("name", "bad", "올바르지 않은 노선 이름입니다.");
        }
        addLog(errors);
    }

    private void addLog(Errors errors) {
        if (errors.getFieldError("name") != null) {
            logger.error(errors.getFieldError("name").getDefaultMessage());
        }
    }
}
