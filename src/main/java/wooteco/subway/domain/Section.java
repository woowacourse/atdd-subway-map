package wooteco.subway.domain;

public class Section {

    private static final int MIN_DISTANCE = 0;

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(final Long id, final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        validateSameStationId(upStationId, downStationId);
        validateDistance(distance);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateSameStationId(final Long upStationId, final Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("상행, 하행 역은 서로 달라야합니다.");
        }
    }

    private void validateDistance(final int distance) {
        if (distance <= MIN_DISTANCE) {
            throw new IllegalArgumentException("구간의 거리는 0보다 커야합니다.");
        }
    }

    public boolean isUpperThan(final Section section) {
        return this.downStationId.equals(section.getUpStationId());
    }

    public boolean isLowerThan(final Section section) {
        return this.upStationId.equals(section.downStationId);
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
}
