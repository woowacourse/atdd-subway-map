package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Table;

@Table
public class Edge {
    private Long preStationId;
    private Long stationId;
    private int distance;
    private int duration;

    public Edge() {
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
}
