package wooteco.subway.dto.section;

public class SectionDeletionRequest {

    private Long lineId;
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
