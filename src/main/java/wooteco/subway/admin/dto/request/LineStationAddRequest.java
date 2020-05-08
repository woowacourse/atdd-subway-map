package wooteco.subway.admin.dto.request;

public class LineStationAddRequest {
    private String lineName;
    private String preStationName;
    private String stationName;

    protected LineStationAddRequest() {
    }

    public LineStationAddRequest(String lineName, String preStationName, String stationName) {
        this.lineName = lineName;
        this.preStationName = preStationName;
        this.stationName = stationName;
    }

    public String getLineName() {
        return lineName;
    }

    public String getPreStationName() {
        return preStationName;
    }

    public String getStationName() {
        return stationName;
    }
}
