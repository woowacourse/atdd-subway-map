package wooteco.subway.exception;

public class DuplicatedNameException extends SubwayException {

    public DuplicatedNameException(final String message) {
        super(message);
    }

    public DuplicatedNameException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
