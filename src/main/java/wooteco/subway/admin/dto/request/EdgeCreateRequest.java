package wooteco.subway.admin.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class EdgeCreateRequest {
    private String preStationName;
    @NotNull(message = "다음 역을 입력해주세요.")
    private String stationName;
    @Min(value = 1, message = "거리를 입력해주세요.")
    private int distance;
    @Min(value = 1, message = "이동 시간을 입력해주세요.")
    private int duration;

    private EdgeCreateRequest() {
    }

    public EdgeCreateRequest(String preStationName, String stationName, int distance, int duration) {
        this.preStationName = preStationName;
        this.stationName = stationName;
        this.distance = distance;
        this.duration = duration;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    public String getPreStationName() {
        return preStationName;
    }

    public String getStationName() {
        return stationName;
    }
}
