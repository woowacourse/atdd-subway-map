package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Column;

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

    public boolean isPreStationOf(Edge requestEdge) {
        return this.stationId == requestEdge.getPreStationId();
    }

    public boolean isSameStationId(Long stationId) {
        return this.stationId == stationId;
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
}
