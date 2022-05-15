package wooteco.subway.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CreateSectionRequest {

    @NotNull(message = "상행역 정보가 입력되지 않았습니다.")
    private Long upStationId;

    @NotNull(message = "하행역 정보가 입력되지 않았습니다.")
    private Long downStationId;

    @Min(value = 1, message = "구간 간 거리는 최소 1이어야합니다.")
    private int distance;

    public CreateSectionRequest() {
    }

    public CreateSectionRequest(Long upStationId,
                                Long downStationId,
                                int distance) {
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

    public void setUpStationId(Long upStationId) {
        this.upStationId = upStationId;
    }

    public void setDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "CreateSectionRequest{" +
                "upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
