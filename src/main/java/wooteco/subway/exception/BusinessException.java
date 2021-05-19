package wooteco.subway.exception;

public abstract class BusinessException extends RuntimeException {

    protected BusinessException(String message) {
        super(message);
    }

    public abstract int status();
}
