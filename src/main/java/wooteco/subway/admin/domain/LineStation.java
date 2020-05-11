package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Column;

public class LineStation {

    @Column("station")
    private Long stationId;
    private Long preStationId;
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

    public boolean isStationIdNull() {
        return stationId == null;
    }

    public boolean isPreStationIdNull() {
        return preStationId == null;
    }

    public boolean isFirstOnLine() {
        return this.preStationId == null;
    }

    public boolean isSameWithPreStationId(LineStation lineStation) {
        return isSameId(lineStation.preStationId);
    }

    public boolean isSameId(Long id) {
        return stationId.equals(id);
    }

    public boolean isSameId(LineStation lineStation) {
        return isSameId(lineStation.stationId);
    }

    public boolean isSamePreStationId(LineStation lineStation) {
        if (preStationId == null) {
            return lineStation.preStationId == null;
        }
        return preStationId.equals(lineStation.preStationId);
    }

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
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
}
