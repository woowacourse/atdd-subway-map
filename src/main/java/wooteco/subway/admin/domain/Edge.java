package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table
public class Edge {
    private Long preStationId;
    private Long stationId;
    private Integer distance;
    private Integer duration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Edge() {
    }

    private Edge(Long preStationId, Long stationId, Integer distance, Integer duration) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Edge ofFirst(Long stationId, Integer distance, Integer duration) {
        return new Edge(null, stationId, distance, duration);
    }

    public static Edge of(Long preStationId, Long stationId, Integer distance, Integer duration) {
        return new Edge(preStationId, stationId, distance, duration);
    }

    public void updatePreStationId(Long preStationId) {
        this.preStationId = preStationId;
        this.updatedAt = LocalDateTime.now();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
