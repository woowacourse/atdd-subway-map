package wooteco.subway.service.dto;

import javax.validation.constraints.NotNull;

public class SectionServiceDeleteRequest {

    @NotNull(message = "lineId를 입력해주세요.")
    private final Long lineId;
    @NotNull(message = "stationId를 입력해주세요.")
    private final Long stationId;

    public SectionServiceDeleteRequest(Long lineId, Long stationId) {
        this.lineId = lineId;
        this.stationId = stationId;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getStationId() {
        return stationId;
    }
}
