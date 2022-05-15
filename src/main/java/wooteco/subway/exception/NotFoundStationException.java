package wooteco.subway.exception;

public class NotFoundStationException extends RuntimeException {
    public NotFoundStationException() {
        super("지하철역을 찾을 수 없습니다.");
    }
}
