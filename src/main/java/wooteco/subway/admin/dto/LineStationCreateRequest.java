package wooteco.subway.admin.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;

public class LineStationCreateRequest {

    private Long preStationId;
    @NotEmpty
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
