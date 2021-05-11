package wooteco.subway.line.domain;

import wooteco.subway.line.exception.SectionException;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long downStationId;
    private final Long upStationId;
    private final int distance;

    public Section(final Long lineId, final Long downStationId, final Long upStationId, final int distance) {
        this(null, lineId, downStationId, upStationId, distance);
    }

    public Section(final Long id, final Long lineId, final Long downStationId,
            final Long upStationId, final int distance) {
        this.id = id;
        this.lineId = lineId;
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public Section updateWith(final Section another) {
        if (this.distance <= another.distance) {
            throw new SectionException("구간의 거리가 노선에 이미 존재하는 관련 구간의 거리보다 깁니다.");
        }
        if (isSame(this.downStationId, another.downStationId)) {
            return new Section(
                    this.id,
                    this.lineId,
                    another.upStationId,
                    this.upStationId,
                    this.distance - another.distance);
        }
        return new Section(
                this.id,
                this.lineId,
                this.downStationId,
                another.downStationId,
                this.distance - another.distance
        );
    }

    public boolean hasSameStations(final Section another) {
        return (isSame(this.downStationId, another.downStationId) && isSame(this.upStationId, another.upStationId)) ||
                (isSame(this.downStationId, another.upStationId) && isSame(this.upStationId, another.downStationId));
    }

    public boolean canConnect(final Section another) {
        return isHigherSection(another) || isLowerSection(another);
    }

    private boolean isHigherSection(final Section another) {
        return isSame(this.upStationId, another.downStationId);
    }

    private boolean isLowerSection(final Section another) {
        return isSame(this.downStationId, another.upStationId);
    }

    public boolean canJoin(final Section another) {
        return isSame(this.downStationId, another.downStationId) || isSame(this.upStationId, another.upStationId);
    }

    private boolean isSame(final Long firstId, final Long secondId) {
        return firstId.equals(secondId);
    }

    public boolean isRelated(final Section another) {
        return canConnect(another) || canJoin(another);
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }
}
