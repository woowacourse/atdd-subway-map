package wooteco.subway.exception;

public class DuplicateNameException extends IllegalArgumentException {

    public DuplicateNameException(final String errorMessage) {
        super(errorMessage);
    }
}
