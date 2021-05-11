package wooteco.subway.dto;

import javax.validation.constraints.NotNull;

public class SectionRequest {
    @NotNull(message = "구간의 상행역 Id는 필수로 입력하여야 합니다.")
    private Long upStationId;
    @NotNull(message = "구간의 하행역 Id는 필수로 입력하여야 합니다.")
    private Long downStationId;
    @NotNull(message = "구간의 거리 정보는 필수로 입력하여야 합니다.")
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
