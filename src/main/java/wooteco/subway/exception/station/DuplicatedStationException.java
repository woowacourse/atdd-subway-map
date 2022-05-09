package wooteco.subway.exception.station;

import wooteco.subway.exception.InvalidInputException;

public class DuplicatedStationException extends InvalidInputException {

    private static final String MESSAGE = "이미 존재하는 역 이름입니다.";

    public DuplicatedStationException() {
        super(MESSAGE);
    }
}
