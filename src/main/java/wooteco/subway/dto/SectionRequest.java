package wooteco.subway.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SectionRequest {

    @NotNull
    @Min(value = 1, message = "상행역은 1 이상이어야 합니다")
    private Long upStationId;

    @NotNull
    @Min(value = 1, message = "하행역은 1 이상이어야 합니다")
    private Long downStationId;

    @NotNull
    @Min(value = 1, message = "거리는 1 이상이여야 합니다")
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
