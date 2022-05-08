package wooteco.subway.application.exception;

public abstract class NotFoundException extends IllegalArgumentException {

    public NotFoundException(String message) {
        super(message);
    }
}
