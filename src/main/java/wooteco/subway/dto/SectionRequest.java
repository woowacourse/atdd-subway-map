package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;

public class SectionRequest {

    @NotBlank(message = "구간 시작은 빈 값일 수 없습니다.")
    private final Long upStationId;
    @NotBlank(message = "구간 끝은 빈 값일 수 없습니다.")
    private final Long downStationId;
    @NotBlank(message = "구간 거리는 빈 값일 수 없습니다.")
    private final int distance;

    private SectionRequest() {
        this(null, null, 0);
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
