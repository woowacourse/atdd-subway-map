package wooteco.subway.presentation.line.dto;

import wooteco.subway.presentation.valid.RightNumberInput;

import java.beans.ConstructorProperties;

public class SectionRequest {

    @RightNumberInput
    private final Long upStationId;
    @RightNumberInput
    private final Long downStationId;
    @RightNumberInput
    private final Long distance;

    @ConstructorProperties({"upStationId", "downStationId", "distance"})
    public SectionRequest(Long upStationId, Long downStationId, Long distance) {
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

    public Long getDistance() {
        return distance;
    }

}
