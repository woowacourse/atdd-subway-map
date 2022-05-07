package wooteco.subway.exception;

public class NotDeletableSectionException extends IllegalArgumentException {

    public NotDeletableSectionException(long stationId) {
        super(String.format("%d의 지하철 역을 제거할 수 없습니다.", stationId));
    }

}
