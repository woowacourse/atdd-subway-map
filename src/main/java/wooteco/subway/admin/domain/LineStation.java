package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Column;

import java.util.Objects;

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

    public boolean isSameStationId(Long id) {
        return Objects.equals(stationId, id);
    }

    public boolean isSamePreStationId(Long id) {
        return Objects.equals(preStationId, id);
    }

    public boolean isFirstStation() {
        return Objects.equals(preStationId, null);
    }

    public boolean isNotFirstStation() {
        return !isFirstStation();
    }

    public boolean isNextStation(LineStation lineStation) {
        return Objects.equals(stationId, lineStation.preStationId);
    }

    public boolean isSameStation(LineStation lineStation) {
        return Objects.equals(preStationId, lineStation.stationId);
    }

    public boolean isDuplicatedPreStation(LineStation lineStation) {
        return Objects.equals(preStationId, lineStation.preStationId);
    }

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
    }

    public void updatePreLineStation(LineStation lineStation) {
        updatePreLineStation(lineStation.stationId);
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
