package wooteco.subway.line.dto;

import javax.validation.constraints.NotEmpty;

public class DeleteSectionRequest {

    @NotEmpty
    private final long lineId;
    @NotEmpty
    private final long stationId;

    public DeleteSectionRequest(long lineId, long stationId) {
        this.lineId = lineId;
        this.stationId = stationId;
    }

    public long getLineId() {
        return lineId;
    }

    public long getStationId() {
        return stationId;
    }

}
