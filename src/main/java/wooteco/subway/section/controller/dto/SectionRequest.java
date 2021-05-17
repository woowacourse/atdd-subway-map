package wooteco.subway.section.controller.dto;

import javax.validation.constraints.Positive;
import wooteco.subway.section.service.dto.SectionCreateDto;

public class SectionRequest {

    @Positive
    private Long upStationId;
    @Positive
    private Long downStationId;
    @Positive
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(final Long upStationId, final Long downStationId, final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public SectionCreateDto toSectionCreateDto(final Long lineId) {
        return SectionCreateDto.ofExistingLine(
                lineId,
                upStationId,
                downStationId,
                distance
        );
    }
}
