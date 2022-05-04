package wooteco.subway.exception;

public class InternalServerException extends RuntimeException {

    public InternalServerException() {
    }

    public InternalServerException(final String message) {
        super(message);
    }

    public InternalServerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InternalServerException(final Throwable cause) {
        super(cause);
    }
}
