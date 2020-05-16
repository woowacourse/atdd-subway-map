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

    public void changePreStationById(Long preStationId) {
        this.preStationId = preStationId;
    }

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
    }

    public boolean isSamePreStationId(Long preStationId) {
        return this.preStationId.equals(preStationId);
    }

    public boolean isSameStationId(Long stationId) {
        return this.stationId.equals(stationId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineStation that = (LineStation) o;
        return distance == that.distance &&
                duration == that.duration &&
                Objects.equals(stationId, that.stationId) &&
                Objects.equals(preStationId, that.preStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, preStationId, distance, duration);
    }
}
