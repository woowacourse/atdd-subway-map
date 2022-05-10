package wooteco.subway.exception.section;

import wooteco.subway.exception.InvalidInputException;

public class NonexistentSectionStationException extends InvalidInputException {

    private static final String MESSAGE = "기존 노선에 존재하는 역이 입력하신 구간에 존재하지 않습니다.";

    public NonexistentSectionStationException() {
        super(MESSAGE);
    }
}
