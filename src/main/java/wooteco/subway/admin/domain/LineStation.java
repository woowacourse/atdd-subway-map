package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Table;

public class LineStation {
    private Long line;
    private Long stationId;
    private Long preStationId;
    private int distance;
    private int duration;

    public LineStation() {
    }

    public LineStation(Long line, Long stationId, Long preStationId, int distance, int duration) {
        this.line = line;
        this.stationId = stationId;
        this.preStationId = preStationId;
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
