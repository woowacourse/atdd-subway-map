package wooteco.subway.exception.section;

import wooteco.subway.exception.InvalidInputException;

public class DuplicatedSectionException extends InvalidInputException {

    private static final String MESSAGE = "이미 존재하는 구간입니다.";

    public DuplicatedSectionException() {
        super(MESSAGE);
    }
}
