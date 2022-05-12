package wooteco.subway.service.dto;

import wooteco.subway.domain.SectionEntity;
import wooteco.subway.ui.request.SectionRequest;

public class SectionDto {

    private final Long lineId;
    private final Long sectionId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionDto(Long lineId, Long sectionId, Long upStationId, Long downDestinationId, int distance) {
        this.lineId = lineId;
        this.sectionId = sectionId;
        this.upStationId = upStationId;
        this.downStationId = downDestinationId;
        this.distance = distance;
    }

    public SectionDto(Long lineId, Long upStationId, Long downDestinationId, int distance) {
        this(lineId, null, upStationId, downDestinationId, distance);
    }

    public static SectionDto from(Long lineId, SectionRequest sectionRequest) {
        return new SectionDto(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
            sectionRequest.getDistance());
    }

    public SectionDto withLineId(Long lineId) {
        return new SectionDto(lineId, sectionId, upStationId, downStationId, distance);
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

    public SectionEntity toEntity() {
        return new SectionEntity(lineId, upStationId, downStationId, distance);
    }
}
