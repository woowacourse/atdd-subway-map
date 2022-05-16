package wooteco.subway.exception.section;

import wooteco.subway.exception.NoSuchContentException;

public class NoSuchSectionException extends NoSuchContentException {
    private static final String MESSAGE = "존재하지 않는 구간입니다.";

    public NoSuchSectionException() {
        super(MESSAGE);
    }
}
