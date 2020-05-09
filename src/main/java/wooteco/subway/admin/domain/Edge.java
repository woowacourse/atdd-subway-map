package wooteco.subway.admin.domain;

public class Edge {
    private Long stationId;
    private Long preStationId;
    private Integer distance;
    private Integer duration;

    public Edge() {
    }

    public Edge(Long preStationId, Long stationId, int distance, int duration) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public boolean hasStartStation() {
        return preStationId == null;
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

    public boolean isPreStationOf(final Edge edge) {
        return this.stationId.equals(edge.preStationId);
    }
}
