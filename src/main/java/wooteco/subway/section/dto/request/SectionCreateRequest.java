package wooteco.subway.section.dto.request;

import wooteco.subway.line.dto.request.LineCreateRequest;

import javax.validation.constraints.NotNull;

public class SectionCreateRequest {
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
    private Integer distance;

    public SectionCreateRequest() {
    }

    public SectionCreateRequest(LineCreateRequest lineCreateRequest) {
        this(lineCreateRequest.getUpStationId(), lineCreateRequest.getDownStationId(), lineCreateRequest.getDistance());
    }

    public SectionCreateRequest(Long upStationId, Long downStationId, Integer distance) {
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

    public Integer getDistance() {
        return distance;
    }
}
