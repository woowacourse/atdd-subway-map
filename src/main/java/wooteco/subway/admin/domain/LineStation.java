package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
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
        return !isFirstStation();
    }

    boolean hasSameId(Long stationId) {
        return this.stationId.equals(stationId);
    }

    boolean hasSamePreStation(Set<LineStation> stations) {
        return stations.stream()
                .anyMatch(station -> station.hasSamePrestationId(station.getPreStationId()));
    }

    boolean hasSamePrestationId(Long preStationId) {
        if (Objects.isNull(this.preStationId) || Objects.isNull(preStationId)) {
            return false;
        }
        return this.preStationId.equals(preStationId);
    }

    private boolean isNotLastStation(Set<LineStation> stations) {
        return stations.stream()
                .anyMatch(lineStation -> lineStation.hasSamePrestationId(stationId));
    }
}
