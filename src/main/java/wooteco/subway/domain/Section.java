package wooteco.subway.domain;

public class Section {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(final Long upStationId, final Long downStationId, final int distance) {
        validateDistance(distance);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateDistance(final int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 거리는 1 이상이어야 합니다.");
        }
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
