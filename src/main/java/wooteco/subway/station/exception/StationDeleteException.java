package wooteco.subway.station.exception;

public class StationDeleteException extends StationException {
    private static final String MESSAGE = "존재하지 않은 역이거나 노선에 이미 등록되어 있는 역은 삭제할 수 없습니다.";

    public StationDeleteException() {
        super(MESSAGE);
    }
}
