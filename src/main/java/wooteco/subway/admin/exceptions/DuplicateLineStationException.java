package wooteco.subway.admin.exceptions;

public class DuplicateLineStationException extends IllegalArgumentException {
    private static final String message = "%d ~ %d의 구간이 이미 존재합니다.";

    public DuplicateLineStationException(Long preStationId, Long stationId) {

    }
}
