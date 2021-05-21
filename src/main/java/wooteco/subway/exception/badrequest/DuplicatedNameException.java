package wooteco.subway.exception.badrequest;

public class DuplicatedNameException extends BadRequest {

    public DuplicatedNameException(final String message) {
        super(message);
    }

    public DuplicatedNameException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
