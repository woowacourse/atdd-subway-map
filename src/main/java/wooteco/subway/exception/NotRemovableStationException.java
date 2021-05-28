package wooteco.subway.exception;

public class NotRemovableStationException extends NotRemovableException {

    private static final String NOT_REMOVABLE_STATION = "등록된 구간이 있는 역은 제거할 수 없습니다.";

    public NotRemovableStationException() {
        super(NOT_REMOVABLE_STATION);
    }
}
