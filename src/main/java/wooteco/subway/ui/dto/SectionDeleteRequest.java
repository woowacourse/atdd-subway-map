package wooteco.subway.ui.dto;

public class SectionDeleteRequest {
    private final Long lineId;
    private final Long stationId;

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
