package wooteco.subway.exception.station;

public class ReferencedStationException extends SubwayStationException {
    private static final String MESSAGE = "해당 역을 참조하는 노선이 있어 삭제가 불가능합니다.";

    public ReferencedStationException() {
        super(MESSAGE);
    }

}
