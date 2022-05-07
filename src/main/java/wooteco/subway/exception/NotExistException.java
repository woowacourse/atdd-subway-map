package wooteco.subway.exception;

public class NotExistException extends IllegalArgumentException {
    public NotExistException(String message) {
        super(message);
    }
}
