package wooteco.subway.dto.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class NameValidator implements ConstraintValidator<Name, String> {

    @Override
    public void initialize(Name subwayNameValidator) {
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        return name.matches("^[가-힣|0-9]+$") && StringUtils.isNotBlank(name);
    }
}