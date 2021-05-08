package wooteco.subway.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.exception.station.StationNameFormatException;
import wooteco.subway.exception.station.StationNameNullException;

import java.util.regex.Pattern;

public class StationValidator implements Validator {

    private static final Pattern PATTERN = Pattern.compile("^[가-힣|0-9]*역$");

    @Override
    public boolean supports(Class<?> clazz) {
        return StationRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StationRequest stationRequest = (StationRequest) target;
        String name = stationRequest.getName();
        if (name == null) {
            throw new StationNameNullException();
        }
        if (!PATTERN.matcher(name).matches()) {
            throw new StationNameFormatException();
        }
    }
}
