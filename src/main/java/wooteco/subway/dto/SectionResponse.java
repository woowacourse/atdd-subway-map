package wooteco.subway.dto;

import wooteco.subway.domain.Section;

public class SectionResponse {
    private final Long id;
    private final Long upStationId;
    private final Long downStationId;
    private final Integer distance;

    private SectionResponse(Long id, Long upStationId, Long downStationId, Integer distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionResponse(Section section) {
        this(section.getId(), section.getUpStationId(), section.getDownStationId(), section.getDistance().getValue());
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

    public Integer getDistance() {
        return distance;
    }
}
