package wooteco.subway.controller.dto;

import wooteco.subway.domain.Section;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class SectionRequest {

    @NotNull
    private final Long upStationId;

    @NotNull
    private final Long downStationId;

    @NotNull
    @Positive
    private final int distance;

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
        return new Section(null, lineId, upStationId, downStationId, distance);
    }
}
