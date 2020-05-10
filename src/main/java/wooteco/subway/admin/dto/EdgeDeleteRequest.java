package wooteco.subway.admin.dto;

public class EdgeDeleteRequest {
    private Long preStationId;
    private Long stationId;

    public EdgeDeleteRequest() {
    }

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
