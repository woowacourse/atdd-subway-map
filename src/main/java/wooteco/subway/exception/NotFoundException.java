package wooteco.subway.exception;

public class NotFoundException extends IllegalArgumentException {
    public NotFoundException(final String msg) {
        super(msg);
    }
}
