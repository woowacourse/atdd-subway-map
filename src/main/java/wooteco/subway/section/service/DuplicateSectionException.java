package wooteco.subway.section.service;

public class DuplicateSectionException extends SectionException {
    private static final String MESSAGE = "이미 존재하는 구간입니다.";

    public DuplicateSectionException() {
        super(MESSAGE);
    }
}
