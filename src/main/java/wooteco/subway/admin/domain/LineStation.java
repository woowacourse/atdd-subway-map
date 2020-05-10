package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Column;

public class LineStation {

    @Column("station")
    private long stationId;
    private Long preStationId;
    private int distance;
    private int duration;

    public LineStation() {
    }

    public LineStation(long stationId, Long preStationId, int distance, int duration) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public boolean isFirstOnLine() {
        return this.preStationId == null;
    }

    public boolean isSameWithPreStationId(LineStation lineStation) {
        return this.stationId == lineStation.preStationId;
    }

    public boolean isSameId(Long id) {
        return this.stationId == id;
    }

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public long getStationId() {
        return stationId;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }
}
