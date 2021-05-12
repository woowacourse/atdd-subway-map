package wooteco.subway.web.dto;

import javax.validation.constraints.Min;
import wooteco.subway.domain.section.Section;

public class SectionRequest {

    @Min(1)
    private Long upStationId;
    @Min(1)
    private Long downStationId;
    @Min(0)
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

    public Section toEntity() {
        return new Section(upStationId, downStationId, distance);
    }
}
