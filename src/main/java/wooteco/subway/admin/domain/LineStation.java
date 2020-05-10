package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.Objects;

public class LineStation {
    @Column("station")
    private Long stationId;
    @Column("pre_station")
    private Long preStationId;
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

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isNotStarting() {
        return !isStarting();
    }

    public boolean isStarting() {
        return Objects.isNull(preStationId);
    }

    public boolean isEqualToStationId(Long stationId) {
        return Objects.equals(this.stationId, stationId);
    }

    public boolean isEqualToPreStationId(Long preStationId) {
        return Objects.equals(this.preStationId, preStationId);
    }
}
