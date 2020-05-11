package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Column;

import java.util.Objects;

public class LineStation {
    @Column("station")
    private Long stationId;
    @Column("pre_station")
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

    public boolean is(Long stationId) {
        return this.stationId.equals(stationId);
    }

    public boolean isFirstLineStation() {
        return preStationId == null;
    }

    public boolean isNotFirstLineStation() {
        return !isFirstLineStation();
    }

    public boolean isPreStationOf(LineStation lineStation) {
        return this.stationId.equals(lineStation.getPreStationId());
    }

    public void updatePreStationId(Long preStationId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineStation that = (LineStation) o;
        return Objects.equals(stationId, that.stationId) &&
                Objects.equals(preStationId, that.preStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, preStationId);
    }
}
