package wooteco.subway.service.dto;

import javax.validation.constraints.NotNull;

public class SectionSaveRequest {

    @NotNull
    private Long lineId;
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
    private Integer distance;

    public SectionSaveRequest() {
    }

    public SectionSaveRequest(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionSaveRequest of(Long lineId, SectionRequest request) {
        return new SectionSaveRequest(lineId, request.getUpStationId(),
                request.getDownStationId(), request.getDistance());
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }
}
