package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.relational.core.mapping.Column;

import wooteco.subway.admin.exception.InvalidLineStationException;

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
        validateLineStation(preStationId, stationId);
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private void validateLineStation(Long preStationId, Long stationId) {
        if (Objects.equals(preStationId, stationId)) {
            throw new InvalidLineStationException("같은 역을 출발지점과 도착지점으로 정할 수 없습니다.");
        }
    }

    public boolean isSameStation(LineStation lineStation) {
        return (this.stationId.equals(lineStation.stationId) && this.preStationId == lineStation.preStationId)
            || (this.preStationId == lineStation.stationId && this.stationId.equals(lineStation.preStationId));
    }

    public boolean isPreStation(Long id) {
        return Objects.equals(preStationId, id);
    }

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
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
}
