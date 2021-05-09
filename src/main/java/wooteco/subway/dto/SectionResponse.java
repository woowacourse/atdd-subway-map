package wooteco.subway.dto;

import wooteco.subway.entity.SectionEntity;

public class SectionResponse {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionResponse(Long id, Long lineId, Long upStationId, Long downStationId,
        int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionResponse(SectionEntity sectionEntity) {
        this(sectionEntity.getId(), sectionEntity.getLineId(), sectionEntity.getUpStationId(),
            sectionEntity.getDownStationId(), sectionEntity.getDistance());
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
