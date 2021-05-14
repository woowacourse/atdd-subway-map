package wooteco.subway.section.exception;

public class SectionInitializationException extends SectionException {
    private static final String MESSAGE = "해당 노선은 등록되지 않은 노선입니다.";

    public SectionInitializationException() {
        super(MESSAGE);
    }
}
