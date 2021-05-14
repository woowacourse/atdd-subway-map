package wooteco.subway.service.dto;

import javax.validation.constraints.NotNull;
import wooteco.subway.controller.dto.request.SectionRequest;

public class CreateSectionDto {

    @NotNull
    private final Long lineId;
    @NotNull
    private final Long upStationId;
    @NotNull
    private final Long downStationId;
    @NotNull
    private final int distance;

    public CreateSectionDto(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static CreateSectionDto of(Long lineId, SectionRequest dto) {
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
