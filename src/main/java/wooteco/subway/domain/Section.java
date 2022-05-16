package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private static final int MIN_DISTANCE = 1;
    private static final String INVALID_DISTANCE_ERROR_MESSAGE = String.format("거리는 %d 이상이어야 합니다.", MIN_DISTANCE);
    private static final String DUPLICATED_SECTIONS_ERROR_MESSAGE = "상행과 하행은 같은 역으로 등록할 수 없습니다.";

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        validSection(upStationId, downStationId, distance);
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this(lineId, upStationId, downStationId, distance);
        this.id = id;
    }

    private void validSection(Long upStationId, Long downStationId, int distance) {
        validDistance(distance);
        validStations(upStationId, downStationId);
    }

    private void validDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException(INVALID_DISTANCE_ERROR_MESSAGE);
        }
    }

    private void validStations(Long upStationId, Long downStationId) {
        if (downStationId.equals(upStationId)) {
            throw new IllegalArgumentException(DUPLICATED_SECTIONS_ERROR_MESSAGE);
        }
    }

    public void reduceDistance(Section other) {
        if (other.distance >= distance) {
            throw new IllegalArgumentException(INVALID_DISTANCE_ERROR_MESSAGE);
        }
        reduceDistance(other.distance);
    }

    private void reduceDistance(int distance) {
        this.distance -= distance;
    }

    public void addDistance(Section other) {
        addDistance(other.distance);
    }

    private void addDistance(int distance) {
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
