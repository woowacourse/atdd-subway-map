package wooteco.subway.exception.section;

import wooteco.subway.exception.InvalidInputException;

public class OnlyOneSectionException extends InvalidInputException {

    private static final String MESSAGE = "라인에 구간이 한개만 존재해 삭제할 수 없습니다.";

    public OnlyOneSectionException() {
        super(MESSAGE);
    }
}
