package wooteco.subway.dto.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class DistanceForSectionValidator implements
    ConstraintValidator<DistanceForSection, Integer> {

    @Override
    public void initialize(DistanceForSection constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer distance, ConstraintValidatorContext context) {
        return distance != null && distance > 0;
    }
}
