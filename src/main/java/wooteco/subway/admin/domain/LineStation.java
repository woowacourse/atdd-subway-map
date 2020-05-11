package wooteco.subway.admin.domain;

import java.util.Objects;

public class LineStation {
    private Long stationId;
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

    public boolean isPreviousOf(LineStation other) {
        return stationId.equals(other.preStationId);
    }

    public boolean isDuplicateOf(LineStation other) {
        return stationId.equals(other.stationId);
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
}
