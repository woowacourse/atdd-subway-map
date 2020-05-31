package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.LineStation;

public class LineStationResponse {
    private Long preStation;
    private Long station;
    private int distance;
    private int duration;

    public LineStationResponse() {
    }

    public LineStationResponse(Long preStation, Long station, int distance, int duration) {
        this.preStation = preStation;
        this.station = station;
        this.distance = distance;
        this.duration = duration;
    }

    public static LineStationResponse of(LineStation lineStation) {
        Long preStationId = lineStation.getPreStation();
        Long stationId = lineStation.getStation();
        int distance = lineStation.getDistance();
        int duration = lineStation.getDuration();

        return new LineStationResponse(preStationId, stationId, distance, duration);
    }

    public Long getPreStation() {
        return preStation;
    }

    public void setPreStation(Long preStation) {
        this.preStation = preStation;
    }

    public Long getStation() {
        return station;
    }

    public void setStation(Long station) {
        this.station = station;
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
