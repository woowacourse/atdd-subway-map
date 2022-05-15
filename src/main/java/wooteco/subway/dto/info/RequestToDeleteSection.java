package wooteco.subway.dto.info;

public class RequestToDeleteSection {
    private final Long lineId;
    private final Long stationId;

    public RequestToDeleteSection(Long lineId, Long stationId) {
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
