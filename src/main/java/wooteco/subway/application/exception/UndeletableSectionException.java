package wooteco.subway.application.exception;

public class UndeletableSectionException extends IllegalArgumentException {

    public UndeletableSectionException(long stationId) {
        super(String.format("%d의 지하철 역을 제거할 수 없습니다.", stationId));
    }

}
