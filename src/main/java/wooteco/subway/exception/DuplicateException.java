package wooteco.subway.exception;

public class DuplicateException extends RuntimeException {

    public DuplicateException(String message, Throwable e) {
        super(message, e);
    }
}
