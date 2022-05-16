package wooteco.subway.service.dto;

import wooteco.subway.domain.Section;

public class SectionDto {

    private Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionDto(final Long upStationId, final Long downStationId, final int distance, final Long lineId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.lineId = lineId;
    }

    public SectionDto(final Long upStationId, final Long downStationId, final int distance) {
        this(upStationId, downStationId, distance, 0L);
    }

    public static SectionDto from(final Section section) {
        return new SectionDto(section.getUpStation().getId(), section.getDownStation().getId(),
                section.getDistance());
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
