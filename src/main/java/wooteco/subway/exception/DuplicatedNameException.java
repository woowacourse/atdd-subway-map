package wooteco.subway.exception;

public class DuplicatedNameException extends SubwayException {

    public DuplicatedNameException(String message) {
        super(message);
    }

    public DuplicatedNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
