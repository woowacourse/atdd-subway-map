package wooteco.subway.line.web;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class LineValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return LineRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LineRequest lineRequest = (LineRequest) target;
        if (lineRequest.getDownStationId().equals(lineRequest.getUpStationId())) {
            errors.rejectValue("downStationId", "duplicatedStation", "중복된 역입니다.");
        }
    }
}
