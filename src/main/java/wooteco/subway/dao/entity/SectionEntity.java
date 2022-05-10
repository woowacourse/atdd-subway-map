package wooteco.subway.dao.entity;

import java.util.Objects;

public class SectionEntity {

    private final Long id;
    private final Long upStationId;
    private final Long downStationId;
    private final Long lineId;
    private final int distance;

    public SectionEntity(Long id, Long upStationId, Long downStationId, Long lineId, int distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.lineId = lineId;
        this.distance = distance;
    }

    public SectionEntity(Long upStationId, Long downStationId, Long lineId, int distance) {
        this(null, upStationId, downStationId, lineId, distance);
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SectionEntity sectionEntity = (SectionEntity) o;
        return distance == sectionEntity.distance && Objects.equals(
                upStationId, sectionEntity.upStationId) && Objects.equals(downStationId, sectionEntity.downStationId)
                && Objects.equals(lineId, sectionEntity.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStationId, downStationId, lineId, distance);
    }
}
