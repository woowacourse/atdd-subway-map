package wooteco.subway.admin.dto;

public class EdgeDeleteRequest {
    private final Long preStationId;
    private final Long stationId;

    public EdgeDeleteRequest(final Long preStationId, final Long stationId) {
        this.preStationId = preStationId;
        this.stationId = stationId;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Long getStationId() {
        return stationId;
    }
}
