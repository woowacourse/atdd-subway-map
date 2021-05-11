package wooteco.subway.dto;

import wooteco.subway.domain.section.Section;

public class SectionResponse {
    private long id;
    private long lineId;
    private long upStationId;
    private long downStationId;
    private int distance;

    public SectionResponse() {
    }

    public SectionResponse(long sectionId, long lineId, Section section) {
        this.id = sectionId;
        this.lineId = lineId;
        this.upStationId = section.getUpStationId();
        this.downStationId = section.getDownStationId();
        this.distance = section.getDistance();
    }

    public long getLineId() {
        return lineId;
    }

    public long getId() {
        return id;
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
