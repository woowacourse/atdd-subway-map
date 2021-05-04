package wooteco.subway.station;

public final class StationException extends IllegalArgumentException {

    public StationException() {
        super("유효하지 않은 역입니다.");
    }

    public StationException(final String s) {
        super(s);
    }

    public StationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public StationException(final Throwable cause) {
        super(cause);
    }
}
