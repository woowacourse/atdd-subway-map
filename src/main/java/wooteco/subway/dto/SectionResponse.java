package wooteco.subway.dto;

import wooteco.subway.domain.section.Section;

public class SectionResponse {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionResponse() {
    }

    public SectionResponse(Long sectionId, Long lineId, Section section) {
        this.id = sectionId;
        this.lineId = lineId;
        this.upStationId = section.getUpStationId();
        this.downStationId = section.getDownStationId();
        this.distance = section.getDistance();
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getId() {
        return id;
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
