package wooteco.subway.service.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SectionDto {

    private final long upStationId;
    private final long downStationId;
    private final int distance;

    public SectionDto(long upStationId, long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
