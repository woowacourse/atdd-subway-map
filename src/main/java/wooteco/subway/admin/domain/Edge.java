package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Table;

@Table
public class Edge {
    private Long preStationId;
    private Long stationId;
    private Integer distance;
    private Integer duration;

    private Edge() {
    }

    public Edge(Long preStationId, Long stationId) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = 0;
        this.duration = 0;
    }

    public Edge(Long preStationId, Long stationId, int distance, int duration) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public void updatePreStationId(Long preStationId) {
        this.preStationId = preStationId;
    }

    public boolean isFirstEdge() {
        return preStationId == null;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Long getStationId() {
        return stationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getDuration() {
        return duration;
    }
}
