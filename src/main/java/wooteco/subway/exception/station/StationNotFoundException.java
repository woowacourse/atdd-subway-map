package wooteco.subway.exception.station;

public class StationNotFoundException extends StationException {

    private static final String MESSAGE = "해당 역이 존재하지 않습니다.";

    public StationNotFoundException() {
        super(MESSAGE);
    }
}
