package wooteco.subway.admin.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class EdgeCreateRequest {
    private Long preStationId;
    @NotNull(message = "다음 역을 입력해주세요.")
    private Long stationId;
    @Min(value = 1, message = "거리를 입력해주세요.")
    private int distance;
    @Min(value = 1, message = "이동 시간을 입력해주세요.")
    private int duration;

    private EdgeCreateRequest() {
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
