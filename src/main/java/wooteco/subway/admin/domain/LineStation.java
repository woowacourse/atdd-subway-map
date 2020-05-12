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

    public boolean isStationNotExist() {
        return stationId == null;
    }

    public boolean isPreStationExist() {
        return preStationId != null;
    }

    public boolean isFirstOnLine() {
        return !isPreStationExist();
    }

    public boolean isPreStationOf(LineStation lineStation) {
        return isSame(lineStation.preStationId);
    }

    public boolean isSame(Long id) {
        return stationId.equals(id);
    }

    public boolean isSame(LineStation lineStation) {
        return isSame(lineStation.stationId);
    }

    public boolean isPreStationSame(LineStation lineStation) {
        if (!isPreStationExist()) {
            return lineStation.isPreStationExist();
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
