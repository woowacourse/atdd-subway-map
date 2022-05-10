package wooteco.subway.dto;

import javax.validation.constraints.Positive;

public class SectionRequest {
    @Positive(message = "상행 종점 id는 양수여야 합니다.")
    private Long upStationId;
    @Positive(message = "하행 종점 id는 양수여야 합니다.")
    private Long downStationId;
    @Positive(message = "두 종점간의 거리는 양수여야 합니다.")
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
