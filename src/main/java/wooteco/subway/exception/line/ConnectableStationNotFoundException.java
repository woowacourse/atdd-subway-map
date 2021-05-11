package wooteco.subway.exception.line;

public class ConnectableStationNotFoundException extends SubwayLineException {
    private static final String MESSAGE = "연결할 수 있는 역이 없습니다.";

    public ConnectableStationNotFoundException() {
        super(MESSAGE);
    }

}
