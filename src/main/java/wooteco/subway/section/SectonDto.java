package wooteco.subway.section;

import wooteco.subway.line.LineRequest;

public class SectonDto {
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectonDto() {}

    private SectonDto(Long lineId, Long upStationId, Long downStationId, int distance){
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public static SectonDto of(Long lineId, LineRequest lineRequest) {
        return new SectonDto(lineId, lineRequest.getUpStationId(),
                lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    public static SectonDto of(Long lineId, wooteco.subway.line.SectionRequest sectionRequest) {
        return new SectonDto(lineId, sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }
}
