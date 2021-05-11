package wooteco.subway.dto;

import javax.validation.constraints.NotNull;

public class SectionRequest {
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
    private int distance;

    private SectionRequest() {
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
