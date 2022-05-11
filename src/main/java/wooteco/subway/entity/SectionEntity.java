package wooteco.subway.entity;

import wooteco.subway.domain.Section;

public class SectionEntity {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionEntity(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionEntity(Long upStationId, Long downStationId, int distance) {
        this(null, null, upStationId, downStationId, distance);
    }

    public static SectionEntity from(Section section) {
        return new SectionEntity(section.getUpStation().getId(),
            section.getDownStation().getId(),
            section.getDistance().getValue());
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
