package wooteco.subway.ui.dto;

import javax.validation.constraints.NotNull;
import wooteco.subway.service.dto.SectionServiceRequest;

public class SectionRequest {

    @NotNull(message = "upStationId를 입력해주세요.")
    private Long upStationId;
    @NotNull(message = "downStationId를 입력해주세요.")
    private Long downStationId;
    @NotNull(message = "distance를 입력해주세요.")
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

    public SectionServiceRequest toServiceRequest() {
        return new SectionServiceRequest(upStationId, downStationId, distance);
    }
}
