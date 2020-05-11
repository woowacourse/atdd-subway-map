package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Table("line_station")
public class LineStation {
    @Column("station")
    private Long stationId;

    @Column("pre_station")
    private Long preStationId;
    private int distance;
    private int duration;

    public LineStation() {
    }

    public LineStation(Long preStationId, Long stationId, int distance, int duration) {
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

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
    }

    boolean isFirstStation() {
        return Objects.isNull(preStationId);

    }

    boolean isNotFirstStatoin() {
        return Objects.nonNull(preStationId);
    }

    boolean hasSamePrestation(LineStation other) {
        if (Objects.isNull(preStationId) || Objects.isNull(other.preStationId)) {
            return false;
        }
        return other.preStationId.equals(preStationId);
    }

    boolean hasSamePreStation(Set<LineStation> stations) {
        return stations.stream()
                .anyMatch(this::hasSamePrestation);
    }

    boolean inBetween(Set<LineStation> stations) {
        return isNotFirstStatoin() && isNotLastStation(stations);
    }

    private boolean isNotLastStation(Set<LineStation> stations) {
        return stations.stream()
                .anyMatch(lineStation -> lineStation.hasSamePrestationId(stationId));
    }

    boolean hasSameId(Long stationId) {
        return this.stationId.equals(stationId);
    }

    boolean hasSamePrestationId(Long preStationId) {
        if (Objects.isNull(this.preStationId) || Objects.isNull(preStationId)) {
            return false;
        }
        return this.preStationId.equals(preStationId);
    }
}
