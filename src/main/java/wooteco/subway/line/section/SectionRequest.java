package wooteco.subway.line.section;

public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
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
