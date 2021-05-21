package wooteco.subway.section;

import wooteco.subway.line.LineRequest;

public class SectionDto {
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionDto() {
    }

    private SectionDto(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionDto of(Long lineId, LineRequest lineRequest) {
        return new SectionDto(lineId, lineRequest.getUpStationId(),
                lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    public static SectionDto of(Long lineId, SectionRequest sectionRequest) {
        return new SectionDto(lineId, sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(), sectionRequest.getDistance());
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
}
