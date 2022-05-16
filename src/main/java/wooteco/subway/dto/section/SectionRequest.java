package wooteco.subway.dto.section;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class SectionRequest {

    @NotNull
    private Long upStationId;

    @NotNull
    private Long downStationId;

    @Positive
    private int distance;

    private SectionRequest() {
    }

    public SectionRequest(final Long upStationId, final Long downStationId, final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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
