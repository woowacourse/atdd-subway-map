package wooteco.subway.dto;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SectionRequest {

    @NotNull(message = "상행역은 비어있을 수 없습니다.")
    private final Long upStationId;

    @NotNull(message = "하행역은 비어있을 수 없습니다.")
    private final Long downStationId;

    @Min(value = 1, message = "거리는 양수이어야 합니다.")
    private final int distance;

    public SectionRequest() {
        this(null, null, 0);
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section toEntity() {
        return new Section(
                new Station(this.upStationId, ""),
                new Station(this.downStationId, ""),
                this.distance
        );
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
