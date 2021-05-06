package wooteco.subway.exception;

public class NotFoundLineException extends RuntimeException {

    public NotFoundLineException(final String errorMessage) {
        super(errorMessage);
    }
}
