package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.LineStation;

public class LineStationRequest {
    private String preStationName;
    private String stationName;
    private int distance;
    private int duration;

    public LineStationRequest() {
    }

    public LineStationRequest(String preStationName, String stationName, int distance, int duration) {
        this.preStationName = preStationName;
        this.stationName = stationName;
        this.distance = distance;
        this.duration = duration;
    }

    public String getPreStationName() {
        return preStationName;
    }

    public String getStationName() {
        return stationName;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

}
