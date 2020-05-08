package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Table;

public class LineStation {
    private Long line;
    private Long preStationId;
    private Long stationId;
    private int distance;
    private int duration;

    public LineStation() {
    }

    public LineStation(Long line, Long preStationId, Long stationId, int distance, int duration) {
        this.line = line;
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public LineStation(Long preStationId, Long stationId, int distance, int duration) {
        this(null, preStationId, stationId, distance, duration);
    }

    public Long getLine() {
        return line;
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

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
    }
}
