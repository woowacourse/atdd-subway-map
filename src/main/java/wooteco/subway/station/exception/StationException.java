package wooteco.subway.station.exception;

public class StationException extends IllegalArgumentException {

    public StationException() {
        super("유효하지 않은 역입니다.");
    }

    public StationException(final String s) {
        super(s);
    }
}
