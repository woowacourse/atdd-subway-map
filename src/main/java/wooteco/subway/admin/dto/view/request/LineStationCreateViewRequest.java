package wooteco.subway.admin.dto.view.request;

import javax.validation.constraints.Min;

public class LineStationCreateViewRequest {
    @Min(value = 1)
    private Long preStationId;
    @Min(value = 1)
    private Long stationId;
    @Min(value = 0)
    private int distance;
    @Min(value = 0)
    private int duration;

    private LineStationCreateViewRequest() {
    }

    public LineStationCreateViewRequest(Long preStationId, Long stationId, int distance, int duration) {
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
