package wooteco.subway.admin.domain;

import java.util.Objects;

public class LineStation {
    public static final long NULL_PRE_STATION_VALUE = 0L;

    private Long stationId;
    private Long preStationId;
    private int distance;
    private int duration;

    public LineStation() {
    }

    public LineStation(Long preStationId, Long stationId, int distance, int duration) {
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
        if (preStationId == null){
            this.preStationId = NULL_PRE_STATION_VALUE;
            return;
        }
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

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
    }

    public boolean isStationIdEquals(final Long id) {
        return this.stationId.equals(id);
    }

    public boolean isPreStationIdEquals(final Long id) {
        return this.preStationId.equals(id);
    }

    @Override
    public String toString() {
        return "LineStation{" +
                "stationId=" + stationId +
                ", preStationId=" + preStationId +
                ", distance=" + distance +
                ", duration=" + duration +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LineStation that = (LineStation) o;
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
