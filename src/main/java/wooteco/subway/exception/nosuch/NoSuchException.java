package wooteco.subway.exception.nosuch;

public class NoSuchException extends IllegalArgumentException {
    public NoSuchException(String message) {
        super(message);
    }
}
