package wooteco.subway.admin.domain;

import java.time.LocalDateTime;

import org.springframework.data.relational.core.mapping.Column;

public class LineStation {
    @Column("pre_station")
    private Long preStationId;
    @Column("station")
    private Long stationId;
    private int distance;
    private int duration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LineStation() {
    }

    public LineStation(Long preStationId, Long stationId, int distance, int duration) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isBaseStation(Long stationId) {
        return this.stationId.equals(stationId);
    }

    public boolean isSameStation(LineStation lineStation) {
        return (this.stationId.equals(lineStation.stationId) && this.preStationId.equals(
            lineStation.preStationId))
            || (this.preStationId.equals(lineStation.stationId) && this.stationId.equals(
            lineStation.preStationId));
    }

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
    }
}
