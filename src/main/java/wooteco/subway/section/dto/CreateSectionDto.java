package wooteco.subway.section.dto;

import wooteco.subway.line.dto.SectionRequest;

public class CreateSectionDto {

    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public CreateSectionDto(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static CreateSectionDto of(final long lineId, final SectionRequest dto) {
        return new CreateSectionDto(lineId, dto.getUpStationId(), dto.getDownStationId(),
            dto.getDistance());
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
