package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class DuplicatedSectionException extends SubwayException {

    private static final String MESSAGE = "중복된 구간입니다.";

    public DuplicatedSectionException() {
        super(MESSAGE);
    }
}
