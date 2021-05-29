package wooteco.subway.dto;

import wooteco.subway.dto.validator.SectionInfo;

public class SectionRequest {

    @SectionInfo
    private Long upStationId;
    @SectionInfo
    private Long downStationId;
    @SectionInfo
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
