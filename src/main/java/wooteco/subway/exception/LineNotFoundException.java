package wooteco.subway.exception;

public class LineNotFoundException extends SubwayException {

    public LineNotFoundException(final String message) {
        super(message);
    }

    public LineNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
