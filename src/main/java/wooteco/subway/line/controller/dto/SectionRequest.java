package wooteco.subway.line.controller.dto;

import wooteco.subway.line.domain.section.Distance;
import wooteco.subway.line.domain.section.Section;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(final Long upStationId, final Long downStationId, final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public Section toEntity(Long lineId) {
        return new Section(lineId, this.upStationId, this.downStationId, new Distance(this.distance));
    }
}
