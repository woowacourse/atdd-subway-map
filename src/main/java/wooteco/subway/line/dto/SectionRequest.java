package wooteco.subway.line.dto;

import javax.validation.constraints.NotNull;

public class SectionRequest {
    @NotNull(message = "upStationI d는 비어있을 수 없습니다.")
    private Long upStationId;

    @NotNull(message = "downStationI d는 비어있을 수 없습니다.")
    private Long downStationId;

    @NotNull(message = "distance 는 비어있을 수 없습니다.")
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(final Long upStationId, final Long downStationId, final int distance) {
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
