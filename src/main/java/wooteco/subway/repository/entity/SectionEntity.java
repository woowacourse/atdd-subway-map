package wooteco.subway.repository.entity;

import wooteco.subway.domain.Section;

public class SectionEntity {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final Integer distance;

    public SectionEntity(final Long id,
                         final Long lineId,
                         final Long upStationId,
                         final Long downStationId,
                         final Integer distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionEntity(final Section section, final Long lineId) {
        this(
                section.getId(),
                lineId,
                section.getUpStation().getId(),
                section.getDownStation().getId(),
                section.getDistance()
        );
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

    public Integer getDistance() {
        return distance;
    }
}
