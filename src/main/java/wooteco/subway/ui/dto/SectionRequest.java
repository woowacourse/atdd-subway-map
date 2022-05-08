package wooteco.subway.ui.dto;

import javax.validation.constraints.Positive;

public class SectionRequest {

    @Positive
    private Long upStationId;
    @Positive
    private Long downStationId;
    @Positive
    private int distance;

    private SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
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
}
