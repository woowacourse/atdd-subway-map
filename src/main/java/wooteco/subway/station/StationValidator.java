package wooteco.subway.station;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class StationValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Station.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
