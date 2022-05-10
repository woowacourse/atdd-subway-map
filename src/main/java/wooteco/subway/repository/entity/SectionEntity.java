package wooteco.subway.repository.entity;

import wooteco.subway.domain.Section;

public class SectionEntity {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionEntity(final Long id,
                         final Long lineId,
                         final Long upStationId,
                         final Long downStationId,
                         final int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionEntity(final Section section) {
        this(
                section.getId(),
                section.getLine().getId(),
                section.getUpStation().getId(),
                section.getDownStation().getId(),
                section.getDistance()
        );
    }

    public SectionEntity fillId(final Long id) {
        return new SectionEntity(id, lineId, upStationId, downStationId, distance);
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
