package wooteco.subway.dto.section;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class SectionCreationRequest {

    @NotNull
    private Long lineId;

    @NotNull
    private Long upStationId;

    @NotNull
    private Long downStationId;

    @Positive
    private int distance;

    private SectionCreationRequest() {
    }

    public SectionCreationRequest(final Long lineId, final Long upStationId, final Long downStationId,
                                  final int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
