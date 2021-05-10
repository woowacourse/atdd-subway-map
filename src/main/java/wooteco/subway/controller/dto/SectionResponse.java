package wooteco.subway.controller.dto;

import wooteco.subway.domain.Section;

public class SectionResponse {
    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionResponse(final Section section) {
        this(section.getId(), section.getLineId(), section.getUpStationId(), section.getDownStationId(),
                section.getDistance());
    }

    public SectionResponse(final Long id, final Long lineId, final Long upStationId, final Long downStationId,
                           final int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
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

    public int getDistance() {
        return distance;
    }
}
