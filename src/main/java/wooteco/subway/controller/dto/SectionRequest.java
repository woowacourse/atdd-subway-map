package wooteco.subway.controller.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SectionRequest {

    @NotNull(message = "상행역을 선택해주세요.")
    private Long upStationId;
    @NotNull(message = "하행역을 선택해주세요.")
    private Long downStationId;
    @Min(value = 1, message = "거리는 1 이상이어야합니다.")
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
