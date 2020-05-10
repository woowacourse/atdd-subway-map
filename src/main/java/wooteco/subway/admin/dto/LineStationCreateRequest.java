package wooteco.subway.admin.dto;

import javax.validation.constraints.NotNull;

import wooteco.subway.admin.domain.LineStation;

public class LineStationCreateRequest {
    @NotNull(message = "이전역을 작성해주세요!")
    private Long preStationId;
    @NotNull(message = "대역을 작성해주세요!")
    private Long stationId;
    @NotNull(message = "거리를 작성해주세요!")
    private Integer distance;
    @NotNull(message = "duration을 작성해주세요!")
    private Integer duration;

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
