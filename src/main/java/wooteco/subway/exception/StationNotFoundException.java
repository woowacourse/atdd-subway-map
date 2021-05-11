package wooteco.subway.exception;

public class StationNotFoundException extends SubwayException {

    public StationNotFoundException(final String message) {
        super(message);
    }

    public StationNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
