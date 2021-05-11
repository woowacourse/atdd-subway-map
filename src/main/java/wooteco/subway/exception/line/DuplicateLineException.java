package wooteco.subway.exception.line;

public class DuplicateLineException extends RuntimeException {

    public DuplicateLineException(final String errorMessage) {
        super(errorMessage);
    }
}
