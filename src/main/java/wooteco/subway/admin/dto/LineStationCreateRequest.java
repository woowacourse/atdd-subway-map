package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.LineStation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class LineStationCreateRequest {
    @NotNull
    private Long preStationId;
    @NotNull
    private Long stationId;
    @PositiveOrZero
    private int distance;
    @PositiveOrZero
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
}
