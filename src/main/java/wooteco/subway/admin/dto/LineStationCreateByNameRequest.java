package wooteco.subway.admin.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public class LineStationCreateByNameRequest {
    private String preStationName;

    @NotBlank
    private String stationName;

    @Positive
    private int distance;

    @Positive
    private int duration;

    public LineStationCreateByNameRequest() {
    }

    public LineStationCreateByNameRequest(String preStationName, String stationName, int distance,
        int duration) {
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
