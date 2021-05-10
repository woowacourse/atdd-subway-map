package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class InvalidSectionException extends SubwayException {

    private static final String MESSAGE = "잘못된 구간입니다.";

    public InvalidSectionException() {
        super(MESSAGE);
    }
}
