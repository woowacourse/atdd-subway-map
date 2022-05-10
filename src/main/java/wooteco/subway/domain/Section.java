package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private static final int DISTANCE_MINIMUM = 0;

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validateDistance(distance);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance <= DISTANCE_MINIMUM) {
            throw new IllegalArgumentException("거리는 0이하의 값을 설정할 수 없습니다.");
        }
    }

    public boolean isSameUpStationId(Section otherSection) {
        return Objects.equals(this.upStationId, otherSection.upStationId);
    }

    public boolean isSameDownStationId(Section otherSection) {
        return Objects.equals(this.downStationId, otherSection.downStationId);
    }

    public boolean isSameStation(Section requestSection) {
        return Objects.equals(this.upStationId, requestSection.downStationId);
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
