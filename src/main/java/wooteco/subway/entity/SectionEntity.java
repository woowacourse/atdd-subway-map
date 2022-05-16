package wooteco.subway.entity;

import wooteco.subway.domain.section.Section;

public class SectionEntity implements Entity {

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

    public SectionEntity(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public static SectionEntity from(Section section, Long lineId) {
        return new SectionEntity(
            section.getId(),
            lineId,
            section.getUpStation().getId(),
            section.getDownStation().getId(),
            section.getDistance().getValue());
    }

    public static SectionEntity from(Section section) {
        return from(section, null);
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
