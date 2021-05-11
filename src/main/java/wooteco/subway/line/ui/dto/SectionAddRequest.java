package wooteco.subway.line.ui.dto;

import java.beans.ConstructorProperties;

public class SectionAddRequest {
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    @ConstructorProperties({"upStationId", "downStationId", "distance"})
    public SectionAddRequest(final Long upStationId, final Long downStationId,  final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
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
