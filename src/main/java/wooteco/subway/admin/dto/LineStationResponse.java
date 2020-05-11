package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.LineStation;

public class LineStationResponse {
    private Long preStationId;
    private String preStationName;
    private Long stationId;
    private String stationName;
    private int distance;
    private int duration;

    public LineStationResponse() {
    }

    public LineStationResponse(Long preStationId, String preStationName, Long stationId, String stationName, int distance, int duration) {
        this.preStationId = preStationId;
        this.preStationName = preStationName;
        this.stationId = stationId;
        this.stationName = stationName;
        this.distance = distance;
        this.duration = duration;
    }

    public static LineStationResponse of(LineStation lineStation, String preStationName, String stationName) {
        return new LineStationResponse(
                lineStation.getPreStationId(),
                preStationName,
                lineStation.getStationId(),
                stationName,
                lineStation.getDistance(),
                lineStation.getDuration());
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

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }
}
