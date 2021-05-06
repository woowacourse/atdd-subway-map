package wooteco.subway.exception;

public class NotFoundLineException extends RuntimeException {

    public NotFoundLineException(String errorMessage) {
        super(errorMessage);
    }
}
