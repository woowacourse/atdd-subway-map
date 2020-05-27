package wooteco.subway.admin.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import wooteco.subway.admin.domain.LineStation;

public class LineStationCreateRequest {

    private Long preStationId;
    @NotNull
    @Range(min = 1, message = "stationId 최솟값은 1 입니다")
    private Long stationId;
    @Range(min = 1, message = "distance 최솟값은 1 입니다")
    private int distance;
    @Range(min = 1, message = "duration 최솟값은 1 입니다")
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

    @Override
    public String toString() {
        return "LineStationCreateRequest{" +
            "preStationId=" + preStationId +
            ", stationId=" + stationId +
            ", distance=" + distance +
            ", duration=" + duration +
            '}';
    }
}
