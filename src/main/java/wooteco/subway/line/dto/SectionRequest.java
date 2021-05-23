package wooteco.subway.line.dto;

import javax.validation.constraints.Positive;

public class SectionRequest {
    @Positive(message = "상행역 입력값이 올바르지 않습니다.")
    private Long upStationId;
    @Positive(message = "하행역 입력값이 올바르지 않습니다.")
    private Long downStationId;
    @Positive(message = "구간의 거리는 양수만 가능합니다.")
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
