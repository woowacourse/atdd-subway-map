package wooteco.subway.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(final String msg) {
        super(msg);
    }
}
