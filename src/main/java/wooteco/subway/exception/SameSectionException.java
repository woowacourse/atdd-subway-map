package wooteco.subway.exception;

public class SameSectionException extends IllegalArgumentException {
    private static final String MESSAGE = "같은 구간이 존재합니다.";

    public SameSectionException() {
        super(MESSAGE);
    }
}
