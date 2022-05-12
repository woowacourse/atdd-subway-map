package wooteco.subway.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class SectionRequest {

    @Positive(message = "시점의 id는 1보다 작을 수 없습니다.")
    @NotNull(message = "시점의 id는 null일 수 없습니다.")
    private final Long upStationId;
    @Positive(message = "종점의 id는 1보다 작을 수 없습니다.")
    @NotNull(message = "종점의 id는 null일 수 없습니다.")
    private final Long downStationId;
    @Positive(message = "구간 거리는 1보다 작을 수 없습니다.")
    private final int distance;

    private SectionRequest() {
        this(null, null, 0);
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
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
