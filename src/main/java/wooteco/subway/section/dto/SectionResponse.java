package wooteco.subway.section.dto;

import wooteco.subway.section.Section;

public class SectionResponse {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;


    public SectionResponse(Section savedSection) {
        this.id = savedSection.getId();
        this.lineId = savedSection.getLineId();
        this.upStationId = savedSection.getUpStationId();
        this.downStationId = savedSection.getDownStationId();
        this.distance = savedSection.getDistance();
    }

    public Long getId() {
        return id;
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
