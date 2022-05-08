package wooteco.subway.exception.section;

import wooteco.subway.exception.NoSuchRecordException;

public class NoSuchSectionException extends NoSuchRecordException {

    private static final String MESSAGE = "구간이 존재하지 않습니다.";

    public NoSuchSectionException() {
        super(MESSAGE);
    }
}
