package wooteco.subway.section.dto;

import javax.validation.constraints.NotNull;

public class DeleteStationDto {

    @NotNull
    private final Long lineId;
    @NotNull
    private final Long stationId;

    public DeleteStationDto(final Long lineId, final Long stationId) {
        this.lineId = lineId;
        this.stationId = stationId;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getStationId() {
        return stationId;
    }
}