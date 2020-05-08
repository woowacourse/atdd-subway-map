package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.LineStation;

public class LineStationCreateRequest {
    private Long stationId;
    private Long preStationId;
    private int distance;
    private int duration;

    public LineStationCreateRequest() {
    }

    public LineStationCreateRequest(Long stationId, Long preStationId, int distance, int duration) {
        this.stationId = stationId;
        this.preStationId = preStationId;
        this.distance = distance;
        this.duration = duration;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Long getStationId() {
        return stationId;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    public LineStation toLineStation() {
        return new LineStation(stationId, preStationId, distance, duration);
    }
}
