package wooteco.subway.exception;

public class StationNotFoundException extends SubwayException {

    public StationNotFoundException(String message) {
        super(message);
    }

    public StationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
