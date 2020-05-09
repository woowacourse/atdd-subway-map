package wooteco.subway.admin.dto;

public class LineStationCreateRequest {
    private Long line;
    private Long preStationId;
    private Long stationId;
    private int distance;
    private int duration;

    public LineStationCreateRequest() {
    }

    public LineStationCreateRequest(Long line, Long preStationId, Long stationId, int distance, int duration) {
        this.line = line;
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public Long getLine() {
        return line;
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
