package wooteco.subway.exception.section;

import wooteco.subway.exception.InvalidRequestException;

public class SectionDuplicatedException extends InvalidRequestException {

    private static final String MESSAGE = "중복된 구간입니다.";

    public SectionDuplicatedException() {
        super(MESSAGE);
    }
}
