package wooteco.subway.exception;

public class NotSplittableSectionException extends IllegalArgumentException {

    public NotSplittableSectionException(Long upStationId, Long downStationId) {
        super(String.format("상행 %d, 하행 %d의 지하철역을 등록할 수 없습니다.", upStationId, downStationId));
    }
}
