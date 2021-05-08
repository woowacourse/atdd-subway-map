package wooteco.subway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import wooteco.subway.dto.request.StationRequest;

import java.util.regex.Pattern;

public class StationValidator implements Validator {

    private static final Pattern PATTERN = Pattern.compile("^[가-힣|A-Z|a-z| 0-9]*역$");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean supports(Class<?> clazz) {
        return StationRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StationRequest stationRequest = (StationRequest) target;
        String name = stationRequest.getName();
        if (name == null) {
            errors.rejectValue("name", "required", "역 이름은 필수로 입력해야합니다.");
            addLog(errors);
            return;
        }
        if (!PATTERN.matcher(name).matches()) {
            errors.rejectValue("name", "bad", "올바르지 않은 역 이름입니다.");
            addLog(errors);
        }
    }

    private void addLog(Errors errors) {
        if (errors.getFieldError("name") != null) {
            logger.error(errors.getFieldError("name").getDefaultMessage());
        }
    }
}
