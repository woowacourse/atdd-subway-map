package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;

import java.util.Objects;

public class Edge {
    @Id
    private Long id;
    private Long stationId;
    private Long preStationId;
    private Integer distance;
    private Integer duration;

    public static Edge startEdge(Edge edge) {
        return new Edge(null, edge.preStationId, 0, 0);
    }

    public Edge() {
    }

    public Edge(Long preStationId, Long stationId, int distance, int duration) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public boolean isStartStation() {
        return preStationId == null;
    }

    public boolean isNotStartStation() {
        return !isStartStation();
    }

    public boolean hasSamePreStation(final Edge edge) {
        return Objects.equals(this.preStationId, edge.preStationId);
    }

    public void changePreStationToStationOf(final Edge edge) {
        this.preStationId = edge.stationId;
    }

    public boolean isSameStationId(final Long stationId) {
        return Objects.equals(this.stationId, stationId);
    }

    public boolean isSamePreStationId(final Long stationId) {
        return Objects.equals(this.preStationId, stationId);
    }

    public void replacePreStation(final Edge edge) {
        this.preStationId = edge.preStationId;
    }

    public Long getStationId() {
        return stationId;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getDuration() {
        return duration;
    }
}
