package wooteco.subway.dto.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class StationForSectionValidator implements ConstraintValidator<StationForSection, Long> {

    @Override
    public void initialize(StationForSection sectionInfoValidator) {
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        return id != null && id > 0;
    }
}
