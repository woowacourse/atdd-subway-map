package wooteco.subway.exception.section;

import wooteco.subway.exception.InvalidSubwayResourceException;

public class DuplicatedSectionException extends InvalidSubwayResourceException {

    private static final String MESSAGE = "상행선과 하행선이 이미 등록되어있습니다.";

    private static final DuplicatedSectionException INSTANCE = new DuplicatedSectionException();

    public static DuplicatedSectionException getInstance() {
        return INSTANCE;
    }

    private DuplicatedSectionException() {
        super(MESSAGE);
    }
}
