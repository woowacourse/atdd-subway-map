package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.LineStation;

public class LineStationResponse {
    private Long preStationId;
    private Long stationId;
    private int distance;
    private int duration;

    public LineStationResponse() {
    }

    public LineStationResponse(Long preStationId, Long stationId, int distance, int duration) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public static LineStationResponse of(LineStation lineStation) {
        Long preStationId = lineStation.getPreStationId();
        Long stationId = lineStation.getStationId();
        int distance = lineStation.getDistance();
        int duration = lineStation.getDuration();

        return new LineStationResponse(preStationId, stationId, distance, duration);
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public void setPreStationId(Long preStationId) {
        this.preStationId = preStationId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
