package wooteco.subway.dao.entity;

public class SectionEntity {
    private final Long id;
    private final Long line_id;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionEntity(Long id, Long line_id, Long upStationId, Long downStationId,
        int distance) {
        this.id = id;
        this.line_id = line_id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Long getLine_id() {
        return line_id;
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
