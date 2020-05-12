package wooteco.subway.admin.dto;

public class EdgeResponse {
    private Long lineId;
    private String lineName;
    private Long preStationId;
    private String preStationName;
    private Long stationId;
    private String stationName;

    private EdgeResponse() {
    }

    public EdgeResponse(final Long lineId, final String lineName, final Long preStationId, final String preStationName, final Long stationId, final String stationName) {
        this.lineId = lineId;
        this.lineName = lineName;
        this.preStationId = preStationId;
        this.preStationName = preStationName;
        this.stationId = stationId;
        this.stationName = stationName;
    }

    public Long getLineId() {
        return lineId;
    }

    public String getLineName() {
        return lineName;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public String getPreStationName() {
        return preStationName;
    }

    public Long getStationId() {
        return stationId;
    }

    public String getStationName() {
        return stationName;
    }
}
