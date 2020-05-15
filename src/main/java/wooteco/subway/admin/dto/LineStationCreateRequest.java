package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.LineStation;

import javax.validation.constraints.NotNull;

public class LineStationCreateRequest {
    private Long preStationId;
    @NotNull(message = "역명이 있어야 합니다.")
    private Long stationId;
    @NotNull(message = "거리가 있어야 합니다.")
    private int distance;
    @NotNull(message = "소요시간이 있어야 합니다.")
    private int duration;

    public LineStationCreateRequest() {
    }

    public LineStationCreateRequest(Long preStationId, Long stationId, int distance, int duration) {
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

    public LineStation toLineStation() {
        return new LineStation(preStationId, stationId, distance, duration);
    }
}
