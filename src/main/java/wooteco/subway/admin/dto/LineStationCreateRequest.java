package wooteco.subway.admin.dto;

import javax.validation.constraints.NotNull;
import wooteco.subway.admin.domain.LineStation;

public class LineStationCreateRequest {
    private Long preStationId;
    @NotNull(message = "도착역을 입력해야 한다.")
    private Long stationId;
    @NotNull(message = "거리를 입력해야 한다.")
    private Integer distance;
    @NotNull(message = "소요 시간을 입력해야 한다.")
    private Integer duration;

    public LineStationCreateRequest() {
    }

    public LineStationCreateRequest(Long preStationId, Long stationId, Integer distance,
            Integer duration) {
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

    public Integer getDistance() {
        return distance;
    }

    public Integer getDuration() {
        return duration;
    }

    public LineStation toLineStation() {
        return new LineStation(preStationId, stationId, distance, duration);
    }
}
