package wooteco.subway.exception.section;

public class InvalidSectionException extends SectionException {

    private static final String MESSAGE = "잘못된 구간입니다.";

    public InvalidSectionException() {
        super(MESSAGE);
    }
}
