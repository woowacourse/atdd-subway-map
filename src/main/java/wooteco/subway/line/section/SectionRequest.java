package wooteco.subway.line.section;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class SectionRequest {

    @NotNull(message = "상행역이 입력되지 않았습니다.")
    private Long upStationId;

    @NotNull(message = "하행역이 입력되지 않았습니다.")
    private Long downStationId;

    @Positive(message = "거리는 양수여야 합니다.")
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(final Long upStationId, final Long downStationId, final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section toEntity(final Long lineId) {
        return Section.Builder()
            .lineId(lineId)
            .upStationId(upStationId)
            .downStationId(downStationId)
            .distance(distance)
            .build();
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
