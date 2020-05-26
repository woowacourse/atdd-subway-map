package wooteco.subway.admin.dto;

import javax.validation.constraints.NotNull;

public class EdgeCreateRequest {
    private String preStationName;
    @NotNull(message = "대상역이 비어있습니다.")
    private String stationName;
    private int distance;
    private int duration;

    public EdgeCreateRequest() {
    }

    public EdgeCreateRequest(String preStationName, @NotNull(message = "대상역이 비어있습니다.") String stationName, int distance, int duration) {
        this.preStationName = preStationName;
        this.stationName = stationName;
        this.distance = distance;
        this.duration = duration;
    }

    public String getPreStationName() {
        return preStationName;
    }

    public String getStationName() {
        return stationName;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }
}
