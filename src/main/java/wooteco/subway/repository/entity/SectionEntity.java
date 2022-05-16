package wooteco.subway.repository.entity;

import wooteco.subway.domain.Section;

public class SectionEntity {

    private final Long id;
    private final Long line_id;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionEntity(Long id, Long line_id, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.line_id = line_id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionEntity of(Long lineId, Section section) {
        return new SectionEntity(section.getId(), lineId, section.getUpStation().getId(), section.getDownStation().getId(),
                section.getDistance());
    }

    public static SectionEntity from(Section section) {
        return new SectionEntity(section.getId(), section.getLineId(), section.getUpStation().getId(),
                section.getDownStation().getId(), section.getDistance());
    }

    public Long getId() {
        return id;
    }

    public Long getLine_id() {
        return line_id;
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
