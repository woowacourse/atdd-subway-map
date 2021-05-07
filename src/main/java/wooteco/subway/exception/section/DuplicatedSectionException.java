package wooteco.subway.exception.section;

public class DuplicatedSectionException extends SectionException {

    private static final String MESSAGE = "중복된 구간입니다.";

    public DuplicatedSectionException() {
        super(MESSAGE);
    }
}
