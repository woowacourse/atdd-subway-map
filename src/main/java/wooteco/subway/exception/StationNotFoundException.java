package wooteco.subway.exception;

public class StationNotFoundException extends SubwayException {
    private static final String MESSAGE = "존재하지 않는 지하철 역 입니다.";

    public StationNotFoundException() {
    }

    public StationNotFoundException(final String message) {
        super(message);
    }

    public StationNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
