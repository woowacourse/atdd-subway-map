package wooteco.subway.exception;

public class NoSuchException extends IllegalArgumentException {
    public NoSuchException(String message) {
        super(message);
    }
}
