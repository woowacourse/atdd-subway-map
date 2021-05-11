package wooteco.subway.exception.station;

public class NameLengthException extends SubwayStationException {
    private static final String MESSAGE = "이름은 0 보다 커야 합니다.";

    public NameLengthException() {
        super(MESSAGE);
    }

}
