package wooteco.subway.exception.section;

import wooteco.subway.exception.InternalLogicConflictException;

public class SectionInternalRemovableConflictException extends InternalLogicConflictException {
    private static final String MESSAGE = "내부 삭제 로직에서 이상이 발생하였습니다.";

    public SectionInternalRemovableConflictException() {
        super(MESSAGE);
    }
}
