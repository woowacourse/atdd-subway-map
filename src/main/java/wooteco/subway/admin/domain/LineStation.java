package wooteco.subway.admin.domain;

import java.util.Objects;
import org.springframework.data.relational.core.mapping.Column;

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

    public boolean isFirst() {
        return Objects.isNull(preStationId);
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

    @Override
    public String toString() {
        return "LineStation{" +
            "stationId=" + stationId +
            ", preStationId=" + preStationId +
            ", distance=" + distance +
            ", duration=" + duration +
            '}';
    }
}
