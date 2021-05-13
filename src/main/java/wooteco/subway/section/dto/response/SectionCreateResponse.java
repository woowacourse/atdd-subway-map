package wooteco.subway.section.dto.response;

import wooteco.subway.section.Section;

public class SectionCreateResponse {
    private long id;
    private long lineId;
    private long upStationId;
    private long downStationId;
    private int distance;

    public SectionCreateResponse() {
    }

    public SectionCreateResponse(Section section) {
        this(section.getId(), section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public SectionCreateResponse(long id, long lineId, long upStationId, long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public long getId() {
        return id;
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
