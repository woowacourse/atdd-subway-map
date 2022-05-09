package wooteco.subway.exception;

public class EmptyResultException extends RuntimeException {
    public EmptyResultException() {
        super();
    }

    public EmptyResultException(String message) {
        super(message);
    }
}
