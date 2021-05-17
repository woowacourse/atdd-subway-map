package wooteco.subway.section.domain;

import wooteco.subway.exception.RequestException;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(final Long id, final Long lineId, final Long upStationId,
            final Long downStationId, final int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section combineWith(final Section another) {
        validateDistance(another);
        if (isSame(this.upStationId, another.upStationId)) {
            return new Section(
                    this.id,
                    this.lineId,
                    another.downStationId,
                    this.downStationId,
                    this.distance - another.distance);
        }
        return new Section(
                this.id,
                this.lineId,
                this.upStationId,
                another.upStationId,
                this.distance - another.distance
        );
    }

    public Section shortenWith(final Section another) {
        if (isSame(this.downStationId, another.upStationId)) {
            return new Section(
                    this.id,
                    this.lineId,
                    this.upStationId,
                    another.downStationId,
                    this.distance + another.distance);
        }
        return new Section(
                this.id,
                this.lineId,
                another.upStationId,
                this.downStationId,
                this.distance + another.distance
        );
    }

    private void validateDistance(final Section another) {
        if (this.distance <= another.distance) {
            throw new RequestException("구간의 거리가 노선에 이미 존재하는 관련 구간의 거리보다 길거나 같습니다.");
        }
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
        return (isSame(this.downStationId, another.downStationId) && isDifferent(this.upStationId, another.upStationId))
                || (isSame(this.upStationId, another.upStationId) && isDifferent(this.downStationId, another.downStationId));
    }

    private boolean isSame(final Long firstId, final Long secondId) {
        return firstId.equals(secondId);
    }

    private boolean isDifferent(final Long firstId, final Long secondId) {
        return !firstId.equals(secondId);
    }

    public boolean isRelated(final Section another) {
        return canConnect(another) || canJoin(another);
    }

    public boolean contains(final Long stationId) {
        return this.upStationId.equals(stationId) || this.downStationId.equals(stationId);
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
