package wooteco.subway.exception;

public class DuplicatedNameException extends IllegalArgumentException {
    public DuplicatedNameException(String message) {
        super(message);
    }
}
