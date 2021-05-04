package wooteco.subway.station;

public final class StationException extends IllegalArgumentException {

    public StationException() {
        super("유효하지 않은 노선입니다.");
    }

    public StationException(String s) {
        super(s);
    }

    public StationException(String message, Throwable cause) {
        super(message, cause);
    }

    public StationException(Throwable cause) {
        super(cause);
    }
}
