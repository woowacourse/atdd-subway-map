package wooteco.subway.admin.domain.line;

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


    public boolean isFirstNode() {
        return Objects.isNull(preStationId);
    }

    public boolean isPreNodeOf(LineStation lineStation) {
        return stationId.equals(lineStation.preStationId);
    }

    public boolean isEqualStationId(Long stationId) {
        return this.stationId.equals(stationId);
    }
}
