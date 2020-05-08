package wooteco.subway.admin.exception;

public class LineStationNotFoundException extends RuntimeException {
    public LineStationNotFoundException(final Long id) {
        super(id + "역을 찾을 수 없습니다.");
    }
}
