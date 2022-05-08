package wooteco.subway.exception.station;

import wooteco.subway.exception.InvalidInputException;

public class StationNotFoundException extends InvalidInputException {

    private static final String MESSAGE = "존재하지 않는 역입니다.";

    public StationNotFoundException() {
        super(MESSAGE);
    }
}
