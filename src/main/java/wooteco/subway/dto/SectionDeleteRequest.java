package wooteco.subway.dto;

public class SectionDeleteRequest {
    private Long lineId;
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
