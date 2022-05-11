package wooteco.subway.service.dto;

public class SectionDto {

    private Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionDto(final Long upStationId, final Long downStationId, final int distance, final Long lineId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.lineId = lineId;
    }

    public SectionDto(final Long upStationId, final Long downStationId, final int distance) {
        this(upStationId, downStationId, distance, 0L);
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
