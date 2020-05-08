package wooteco.subway.admin.dto;

public class EdgeCreateRequest {
    private Long preStationId;
    private Long stationId;
    private int distance;
    private int duration;

    public EdgeCreateRequest() {
    }

    public EdgeCreateRequest(Long preStationId, Long stationId, int distance, int duration) {
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
