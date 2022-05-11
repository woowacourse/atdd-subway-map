package wooteco.subway.ui.dto;

import wooteco.subway.domain.Section;

public class SectionRequest {
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
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

    public Long getLineId() {
        return lineId;
    }

    public Section toSection() {
        return new Section(lineId, upStationId, downStationId, distance);
    }
}