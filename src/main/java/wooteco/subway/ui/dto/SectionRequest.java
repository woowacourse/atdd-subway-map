package wooteco.subway.ui.dto;

import java.beans.ConstructorProperties;

public class SectionRequest {
    private final Long upStationId;
    private final Long downStationId;
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
