package wooteco.subway.dto;

import javax.validation.constraints.NotNull;

public class SectionRequest {
    @NotNull(message = "상행 종점 id 값이 누락되었습니다.")
    private Long upStationId;
    @NotNull(message = "하행 종점 id 값이 누락되었습니다.")
    private Long downStationId;
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
