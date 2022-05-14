package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SectionRequest {

    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
    private int distance;

    public SectionRequest() {
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
