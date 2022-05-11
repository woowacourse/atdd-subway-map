package wooteco.subway.entity;

import java.util.Objects;

public class SectionEntity {

    private static final int MIN_DISTANCE = 1;

    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionEntity(Long lineId,
                         Long upStationId,
                         Long downStationId,
                         int distance) {
        validateSection(lineId, upStationId, downStationId, distance);
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        validateNotNull(lineId);
        validateNotNull(upStationId);
        validateNotNull(downStationId);
        validateDistance(distance);
    }

    private void validateNotNull(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("필요한 정보가 입력되지 않았습니다.");
        }
    }

    private void validateDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException("구간의 길이는 1이상이어야 합니다.");
        }
    }

    public Long getLineId() {
        return lineId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SectionEntity that = (SectionEntity) o;
        return distance == that.distance
                && Objects.equals(lineId, that.lineId)
                && Objects.equals(upStationId, that.upStationId)
                && Objects.equals(downStationId, that.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "SectionEntity{" +
                "lineId=" + lineId +
                ", upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
