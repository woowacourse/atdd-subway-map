package wooteco.subway.section.dto;

import javax.validation.constraints.NotNull;
import wooteco.subway.line.dto.DeleteSectionRequest;

public class DeleteStationOnSectionDto {

    @NotNull
    private final Long lineId;
    @NotNull
    private final Long stationId;

    public DeleteStationOnSectionDto(final Long lineId, final Long stationId) {
        this.lineId = lineId;
        this.stationId = stationId;
    }

    public  DeleteStationOnSectionDto(final DeleteSectionRequest deleteSectionRequest) {
        this(deleteSectionRequest.getLineId(), deleteSectionRequest.getStationId());
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getStationId() {
        return stationId;
    }
}