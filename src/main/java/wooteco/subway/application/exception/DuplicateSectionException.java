package wooteco.subway.application.exception;

public class DuplicateSectionException extends DuplicateException {

    public DuplicateSectionException(long lineId, long upStationId, long downStationId) {
        super(String.format("%d와 %d 아이디의 지하철 역은 이미 %d 노선에 구간으로 등록되어 있습니다.",
            upStationId, downStationId, lineId));
    }
}
