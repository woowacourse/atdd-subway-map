package wooteco.subway.section.exception;

public class SectionNotFoundException extends SectionException {
    private static final String MESSAGE = "구간을 찾지 못했습니다.";

    public SectionNotFoundException() {
        super(MESSAGE);
    }
}
