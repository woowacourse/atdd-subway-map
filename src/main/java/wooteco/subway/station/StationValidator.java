package wooteco.subway.station;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import wooteco.subway.station.controller.StationRequest;

import java.util.regex.Pattern;

public class StationValidator implements Validator {
    private final static Pattern STATION_NAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]*역$");

    @Override
    public boolean supports(Class<?> clazz) {
        return StationRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StationRequest stationRequest = (StationRequest) target;
        String name = stationRequest.getName();
        if (!STATION_NAME_PATTERN.matcher(name).matches()) {
            errors.rejectValue("name", "invalidNameInput", "지하철 역 이름이 잘못되었습니다.");
        }
    }
}
