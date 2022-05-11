package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this(lineId, upStationId, downStationId, distance);
        this.id = id;
    }

    public void reduceDistance(Section other) {
        if (distance >= other.distance) {
            throw new IllegalArgumentException("distance 는 0 이하가 될 수 없습니다.");
        }
        reduceDistance(other.distance);
    }

    private void reduceDistance(int distance) {
        this.distance -= distance;
    }

    public void addDistance(int distance) {
        this.distance += distance;
    }

    public boolean isSameDownStationId(Section section) {
        return isSameDownStationId(section.downStationId);
    }

    public boolean isSameDownStationId(Long id) {
        return id.equals(downStationId);
    }

    public boolean isSameUpStationId(Section section) {
        return isSameUpStationId(section.upStationId);
    }

    public boolean isSameUpStationId(Long id) {
        return id.equals(upStationId);
    }

    public void updateUpStationId(Long id) {
        this.upStationId = id;
    }

    public Long getId() {
        return id;
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
        if (!(o instanceof Section)) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(lineId,
                section.lineId) && Objects.equals(upStationId, section.upStationId) && Objects.equals(
                downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", lineId=" + lineId +
                ", upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
