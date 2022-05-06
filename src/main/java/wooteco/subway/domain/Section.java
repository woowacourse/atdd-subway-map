package wooteco.subway.domain;

public class Section {

    private final Long id;
    private final long lineId;
    private final long upStationId;
    private final long downStationId;
    private final int distance;

    public Section(final Long id, final long lineId, final long upStationId, final long downStationId,
                   final int distance) {
        validatePositiveDistance(distance);
        validateDuplicateStation(upStationId, downStationId);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validatePositiveDistance(final int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간의 길이는 양수만 들어올 수 있습니다.");
        }
    }

    private void validateDuplicateStation(final long upStationId, final long downStationId) {
        if (upStationId == downStationId) {
            throw new IllegalArgumentException("upstation과 downstation은 중복될 수 없습니다.");
        }
    }

    public Section(final Long lineId, final long upStationId, final long downStationId, final int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(final Long id, final Section section) {
        this(id, section.lineId, section.upStationId, section.downStationId, section.distance);
    }

    public boolean isSameUpStationAndDownStation(final long upStationId, final long downStationId) {
        return (this.upStationId == upStationId && this.downStationId == downStationId) ||
                (this.upStationId == downStationId && this.downStationId == upStationId);
    }

    public Long getId() {
        return id;
    }

    public long getLineId() {
        return lineId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
