package wooteco.subway.exception;

public class NoSuchSectionException extends NoSuchException {
    private static final String MESSAGE = "구간이 존재하지 않습니다.";

    public NoSuchSectionException() {
        super(MESSAGE);
    }
}
