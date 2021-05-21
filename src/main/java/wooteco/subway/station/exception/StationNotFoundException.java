package wooteco.subway.station.exception;

public class StationNotFoundException extends StationException {
    public static final String MESSAGE = "역을 찾지 못했습니다.";

    public StationNotFoundException() {
        super(MESSAGE);
    }
}
