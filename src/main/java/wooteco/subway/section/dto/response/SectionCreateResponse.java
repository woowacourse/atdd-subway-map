package wooteco.subway.section.dto.response;

import wooteco.subway.section.Section;

public class SectionCreateResponse {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public SectionCreateResponse() {
    }

    public SectionCreateResponse(Section section) {
        this(section.getId(), section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public SectionCreateResponse(Long id, Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public Integer getDistance() {
        return distance;
    }
}
