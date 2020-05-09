package wooteco.subway.admin.dto;

import javax.validation.constraints.NotNull;

public class EdgeDeleteRequest {
    @NotNull(message = "지우려는 역의 값이 비어있습니다.")
    private Long stationId;

    public EdgeDeleteRequest() {
    }

    public EdgeDeleteRequest(final Long stationId) {
        this.stationId = stationId;
    }

    public Long getStationId() {
        return stationId;
    }
}
