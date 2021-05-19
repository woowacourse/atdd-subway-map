package wooteco.subway.section.service.dto;


import wooteco.subway.section.domain.Section;

public class SectionDto {

    private final Long lineId;
    private final Long downStationId;
    private final Long upStationId;
    private final int distance;

    private SectionDto(final Long lineId, final Long downStationId, final Long upStationId, final int distance) {
        this.lineId = lineId;
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public static SectionDto of(final Section section) {
        return new SectionDto(
                section.getLineId(),
                section.getDownStationId(),
                section.getUpStationId(),
                section.getDistance()
        );
    }
}
