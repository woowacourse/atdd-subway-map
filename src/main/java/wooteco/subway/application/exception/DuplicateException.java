package wooteco.subway.application.exception;

public abstract class DuplicateException extends IllegalArgumentException {

    public DuplicateException(String message) {
        super(message);
    }
}
