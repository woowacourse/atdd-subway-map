package wooteco.subway.exception;

public class SectionNotFoundException extends RuntimeException {
    public SectionNotFoundException(final String message) {
        super(message);
    }
}
