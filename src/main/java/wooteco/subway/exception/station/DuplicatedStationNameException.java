package wooteco.subway.exception.station;

import wooteco.subway.exception.InvalidSubwayResourceException;

public class DuplicatedStationNameException extends InvalidSubwayResourceException {

    private static final String MESSAGE = "이미 존재하는 역 이름입니다.";

    public DuplicatedStationNameException() {
        super(MESSAGE);
    }
}
