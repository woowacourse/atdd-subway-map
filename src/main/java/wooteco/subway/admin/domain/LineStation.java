package wooteco.subway.admin.domain;

import java.util.Objects;

public class LineStation {
    private Long preStation;
    private Long station;
    private int distance;
    private int duration;

    public LineStation(Long preStation, Long station, int distance, int duration) {
        this.preStation = preStation;
        this.station = station;
        this.distance = distance;
        this.duration = duration;
    }

    public Long getPreStation() {
        return preStation;
    }

    public Long getStation() {
        return station;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    public void updatePreLineStation(Long preStationId) {
        this.preStation = preStationId;
    }

    public boolean isLineStationOf(Long preStationId, Long stationId) {
        return this.preStation == preStationId && this.station == stationId
                || this.preStation == stationId && this.station == preStationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineStation that = (LineStation) o;
        return Objects.equals(preStation, that.preStation) &&
                Objects.equals(station, that.station);
    }

    @Override
    public int hashCode() {
        return Objects.hash(preStation, station);
    }
}
