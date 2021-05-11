package wooteco.subway.line.ui.dto;

import java.beans.ConstructorProperties;

public class SectionAddRequest {
    private Long downStationId;
    private Long upStationId;
    private int distance;

    @ConstructorProperties({"downStationId", "upStationId", "distance"})

    public SectionAddRequest(final Long downStationId, final Long upStationId, final int distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }
}
