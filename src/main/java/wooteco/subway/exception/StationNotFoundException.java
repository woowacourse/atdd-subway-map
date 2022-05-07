package wooteco.subway.exception;

public class StationNotFoundException extends RuntimeException {
    public StationNotFoundException(final String msg) {
        super(msg);
    }
}
