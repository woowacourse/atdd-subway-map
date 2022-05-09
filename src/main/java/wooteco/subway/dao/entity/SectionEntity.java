package wooteco.subway.dao.entity;

public class SectionEntity {
    private final Long id;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionEntity(Long id, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
