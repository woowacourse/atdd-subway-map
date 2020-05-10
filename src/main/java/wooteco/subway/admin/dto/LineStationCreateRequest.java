package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.LineStation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class LineStationCreateRequest {
    private Long preStationId;
    @NotNull(message = "현재역을 입력해주세요.")
    private Long stationId;
    @PositiveOrZero(message = "구간 거리를 정확히 입력해주세요.")
    private int distance;
    @PositiveOrZero(message = "소요 시간을 정확히 입력해주세요.")
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
