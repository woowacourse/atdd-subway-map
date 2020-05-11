package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Column;

public class LineStation {

    @Column("pre_station_id")
    private Long preStationId;
    @Column("station_id")
    private Long stationId;
    private int distance;
    private int duration;

    public LineStation() {
    }

    public LineStation(Long preStationId, Long stationId, int distance, int duration) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public void updatePreStationId(Long preStationId) {
        this.preStationId = preStationId;
    }

    public boolean isPreStationOf(LineStation requestLineStation) {
        return this.stationId == requestLineStation.getPreStationId();
    }

    public boolean isSameStationId(Long stationId) {
        return this.stationId == stationId;
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

    public void updatePreLineStationId(Long preStationId) {
        this.preStationId = preStationId;
    }
}
