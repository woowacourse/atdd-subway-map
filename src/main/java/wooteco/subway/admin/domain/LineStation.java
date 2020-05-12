package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Table;

@Table("LINE_STATION")
public class LineStation {
    private Long stationId;
    private Long preStationId;
    private int distance;
    private int duration;

    private LineStation() {
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

    public boolean isStartStation() {
        return (this.preStationId == null);
    }
}
