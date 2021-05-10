package wooteco.subway.exception;

public class DuplicateLineException extends RuntimeException {

    public DuplicateLineException(final String errorMessage) {
        super(errorMessage);
    }
}
