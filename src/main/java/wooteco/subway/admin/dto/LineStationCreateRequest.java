package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.LineStation;

public class LineStationCreateRequest {
    private Long preStation;
    private Long station;
    private int distance;
    private int duration;

    public LineStationCreateRequest() {
    }

    public LineStationCreateRequest(Long preStation, Long station, int distance, int duration) {
        this.preStation = preStation;
        this.station = station;
        this.distance = distance;
        this.duration = duration;
    }

    public Long getPreStation() {
        return preStation;
    }

    public Long getStation() {
        return station;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    public LineStation toLineStation() {
        return new LineStation(preStation, station, distance, duration);
    }
}
