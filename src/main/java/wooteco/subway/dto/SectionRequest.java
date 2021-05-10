package wooteco.subway.dto;

import wooteco.subway.domain.section.Section;

public class SectionRequest {
    private long upStationId;
    private long downStationId;
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(long upStationId, long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section createSection() {
        return new Section(this.upStationId, this.downStationId, this.distance);
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
