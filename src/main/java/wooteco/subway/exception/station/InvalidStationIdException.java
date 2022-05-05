package wooteco.subway.exception.station;

import wooteco.subway.exception.InvalidSubwayResourceException;

public class InvalidStationIdException extends InvalidSubwayResourceException {

    private static final String MESSAGE = "존재하지 않는 역입니다.";

    public InvalidStationIdException() {
        super(MESSAGE);
    }
}
