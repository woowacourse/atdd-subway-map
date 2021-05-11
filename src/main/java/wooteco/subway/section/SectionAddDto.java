package wooteco.subway.section;

import wooteco.subway.line.SectionRequest;

public class SectionAddDto {

    private final long lineId;
    private final long upStationId;
    private final long downStationId;
    private final int distance;

    public SectionAddDto(long lineId, long upStationId, long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionAddDto(Long id, SectionRequest sectionRequest) {
        this(id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
            sectionRequest.getDistance());
    }

    public long getLineId() {
        return lineId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
