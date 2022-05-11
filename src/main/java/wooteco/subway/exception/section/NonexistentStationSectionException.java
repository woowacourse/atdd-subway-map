package wooteco.subway.exception.section;

import wooteco.subway.exception.InvalidInputException;

public class NonexistentStationSectionException extends InvalidInputException {

    private static final String MESSAGE = "입력하신 지하철 역이 구간에 존재하지 않습니다.";

    public NonexistentStationSectionException() {
        super(MESSAGE);
    }
}
