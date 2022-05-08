package wooteco.subway.application.exception;

public class UndeletableSectionException extends IllegalArgumentException {

    public UndeletableSectionException(long lineId, long stationId) {
        super(String.format("노선 %d에서 %d의 지하철 역을 제거할 수 없습니다.", lineId, stationId));
    }

}
