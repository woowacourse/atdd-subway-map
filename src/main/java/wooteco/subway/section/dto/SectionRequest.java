package wooteco.subway.section.dto;

import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.section.Section;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionRequest(LineRequest lineRequest) {
        this(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
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
