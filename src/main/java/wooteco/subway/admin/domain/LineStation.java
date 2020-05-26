package wooteco.subway.admin.domain;

import java.util.Optional;

public class LineStation {
    private Long stationId;
    private Long preStationId;
    private int distance;
    private int duration;

    private LineStation() {
    }

    public LineStation(Long preStationId, Long stationId, int distance, int duration) {
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
        this.preStationId = Optional.ofNullable(preStationId)
            .orElse(LineStations.START_STATION);
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
