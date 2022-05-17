package wooteco.subway.service.dto;

import javax.validation.constraints.NotNull;

public class SectionDeleteRequest {

    @NotNull
    private Long lineId;
    @NotNull
    private Long stationId;

    public SectionDeleteRequest() {
    }

    public SectionDeleteRequest(Long lineId, Long stationId) {
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
