package wooteco.subway.web.validate;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import wooteco.subway.exception.line.InsufficientLineInformationException;
import wooteco.subway.web.request.LineRequest;

import java.util.Objects;

public class LineValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return LineRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LineRequest lineRequest = (LineRequest) target;

        if (Objects.isNull(lineRequest.getUpStationId()) || Objects.isNull(lineRequest.getDownStationId())) {
            throw new InsufficientLineInformationException();
        }

        if (lineRequest.getDownStationId().equals(lineRequest.getUpStationId())) {
            errors.rejectValue("downStationId", "duplicatedStation", "중복된 역입니다.");
        }
    }
}
