package wooteco.subway.dto;

import wooteco.subway.dto.validator.DistanceForSection;
import wooteco.subway.dto.validator.StationForSection;

public class SectionRequest {

    @StationForSection
    private Long upStationId;
    @StationForSection
    private Long downStationId;
    @DistanceForSection
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
