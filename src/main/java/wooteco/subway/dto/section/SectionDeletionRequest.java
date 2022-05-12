package wooteco.subway.dto.section;

import javax.validation.constraints.NotNull;

public class SectionDeletionRequest {

    @NotNull
    private Long lineId;

    @NotNull
    private Long stationId;

    private SectionDeletionRequest() {
    }

    public SectionDeletionRequest(final Long lineId, final Long stationId) {
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
