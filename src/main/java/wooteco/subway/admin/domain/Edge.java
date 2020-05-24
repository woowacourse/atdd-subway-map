package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Column;

import java.util.Objects;

public class Edge {

    @Column("pre_station_id")
    private Long preStationId;
    @Column("station_id")
    private Long stationId;
    private int distance;
    private int duration;

    public Edge() {
    }

    public Edge(Long preStationId, Long stationId, int distance, int duration) {
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

    public void updatePreStationId(Long preStationId) {
        this.preStationId = preStationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return distance == edge.distance &&
                duration == edge.duration &&
                Objects.equals(preStationId, edge.preStationId) &&
                Objects.equals(stationId, edge.stationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(preStationId, stationId, distance, duration);
    }

}
