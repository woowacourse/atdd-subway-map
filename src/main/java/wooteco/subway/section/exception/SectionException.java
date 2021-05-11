package wooteco.subway.section.exception;

public class SectionException extends IllegalArgumentException {

    public SectionException() {
        super("유효하지 않은 구간입니다.");
    }

    public SectionException(final String s) {
        super(s);
    }

    public SectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SectionException(final Throwable cause) {
        super(cause);
    }
}
