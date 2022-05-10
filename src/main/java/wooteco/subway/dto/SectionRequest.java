package wooteco.subway.dto;

import wooteco.subway.domain.Section;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    private SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, Integer distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionRequest(Section section) {
        this(section.getUpStationId(), section.getDownStationId(), section.getDistance().getValue());
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

    @Override
    public String toString() {
        return "SectionRequest{" +
                "upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
