package wooteco.subway.admin.dto;

import javax.validation.constraints.NotNull;

public class EdgeDeleteRequest {
    private Long preStationId;
    @NotNull
    private Long stationId;

    public EdgeDeleteRequest() {
    }

    public EdgeDeleteRequest(Long preStationId, Long stationId) {
        this.preStationId = preStationId;
        this.stationId = stationId;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Long getStationId() {
        return stationId;
    }
}
