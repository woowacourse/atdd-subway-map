package wooteco.subway.admin.dto;

import javax.validation.constraints.Min;
import wooteco.subway.admin.domain.LineStation;

public class LineStationCreateRequest {

    private Long preStationId;
    private Long stationId;
    private String preStationName;
    private String stationName;
    @Min(1)
    private int distance;
    @Min(1)
    private int duration;

    public LineStationCreateRequest() {
    }

    public LineStationCreateRequest(Long preStationId, Long stationId, int distance, int duration) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public LineStation toLineStation() {
        return new LineStation(preStationId, stationId, distance, duration);
    }

    public boolean hasStationId() {
        return stationId != null;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Long getStationId() {
        return stationId;
    }

    public String getPreStationName() {
        return preStationName;
    }

    public String getStationName() {
        return stationName;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "LineStationCreateRequest{" +
            "preStationId=" + preStationId +
            ", stationId=" + stationId +
            ", preStationName='" + preStationName + '\'' +
            ", stationName='" + stationName + '\'' +
            ", distance=" + distance +
            ", duration=" + duration +
            '}';
    }
}
