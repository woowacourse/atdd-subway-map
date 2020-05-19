package wooteco.subway.admin.dto.req;

import javax.validation.constraints.NotNull;

import wooteco.subway.admin.domain.LineStation;

public class LineStationCreateRequest {
    @NotNull(message = "prestationId는 Null이 될 수 없습니다. 첫역의 경우 stationId와 동일하게 입력해주세요.")
    private Long preStationId;
    private Long stationId;
    private int distance;
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
        return new LineStation(stationId, preStationId, distance, duration);
    }
}
