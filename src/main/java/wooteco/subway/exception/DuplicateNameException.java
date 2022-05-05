package wooteco.subway.exception;

public class DuplicateNameException extends RuntimeException {

    public DuplicateNameException(final String errorMessage) {
        super(errorMessage);
    }
}
