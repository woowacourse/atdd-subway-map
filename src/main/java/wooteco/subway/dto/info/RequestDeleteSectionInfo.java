package wooteco.subway.dto.info;

public class RequestDeleteSectionInfo {
    private final Long lineId;
    private final Long stationId;

    public RequestDeleteSectionInfo(Long lineId, Long stationId) {
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
