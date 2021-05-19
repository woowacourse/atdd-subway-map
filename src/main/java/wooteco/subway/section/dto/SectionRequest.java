package wooteco.subway.section.dto;

import wooteco.subway.section.domain.Section;

import javax.validation.constraints.Positive;

public class SectionRequest {
    @Positive
    private Long upStationId;
    @Positive
    private Long downStationId;
    @Positive
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

    public Section toSection(final Long lineId) {
        return new Section(lineId, upStationId, downStationId, distance);
    }
}
