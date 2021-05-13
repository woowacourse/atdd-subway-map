package wooteco.subway.exception;

public class SubwayException extends RuntimeException {

    public SubwayException() {
    }

    public SubwayException(final String message) {
        super(message);
    }

    public SubwayException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
