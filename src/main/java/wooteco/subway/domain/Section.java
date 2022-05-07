package wooteco.subway.domain;

public class Section {

    private static final int MIN_DISTANCE = 1;

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        validate(upStationId, downStationId, distance);
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validate(final Long upStationId, final Long downStationId, final int distance) {
        validateEndStation(upStationId, downStationId);
        validateDistance(distance);
    }

    private void validateEndStation(final Long upStationId, final Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("두 종점이 동일합니다.");
        }
    }

    private void validateDistance(final int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException("두 종점간의 거리가 유효하지 않습니다.");
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
}
