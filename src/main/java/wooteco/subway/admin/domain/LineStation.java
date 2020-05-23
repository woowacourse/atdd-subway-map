package wooteco.subway.admin.domain;

import java.util.Objects;

import org.springframework.data.relational.core.mapping.Table;

@Table("LINE_STATION")
public class LineStation {
    private Long stationId;
    private Long preStationId;
    private int distance;
    private int duration;

    private LineStation() {
    }

    public LineStation(Long preStationId, Long stationId, int distance, int duration) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
    }

    public void updatePreStationIdWithIdOf(LineStation lineStation) {
        this.preStationId = lineStation.stationId;
    }

    public boolean isStartStation() {
        return (this.preStationId == null);
    }

    public boolean hasSamePreStationIdWith(LineStation lineStation) {
        return this.preStationId.equals(lineStation.getPreStationId());
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LineStation station = (LineStation)o;
        return Objects.equals(stationId, station.stationId) &&
            Objects.equals(preStationId, station.preStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, preStationId);
    }
}
