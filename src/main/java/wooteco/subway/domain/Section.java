package wooteco.subway.domain;

import wooteco.subway.exception.ExceptionMessage;

public class Section {

    private final Long id;
    private final Long line_id;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(Long id, Long line_id, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.line_id = line_id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long line_id, Long upStationId, Long downStationId, int distance) {
        this(null, line_id, upStationId, downStationId, distance);
    }

    public boolean isForDivide(Section other) {
        return isDownDivide(other) || isUpDivide(other);
    }

    private boolean isDownDivide(Section other) {
        return this.downStationId.equals(other.downStationId);
    }

    private boolean isUpDivide(Section other) {
        return this.upStationId.equals(other.upStationId);
    }

    public Section divideFrom(Section other) {
        checkStationsNotSame(other);
        checkDistance(other);
        int distanceGap = this.distance - other.distance;
        if (isUpDivide(other)) {
            return new Section(id, line_id, other.downStationId, downStationId, distanceGap);
        }
        if (isDownDivide(other)) {
            return new Section(id, line_id, upStationId, other.upStationId, distanceGap);
        }
        throw new IllegalArgumentException(ExceptionMessage.INVALID_DIVIDE_SECTION.getContent());
    }

    private void checkStationsNotSame(Section other) {
        if (upStationId.equals(other.upStationId) && downStationId.equals(other.downStationId)) {
            throw new IllegalArgumentException(ExceptionMessage.SAME_STATIONS_SECTION.getContent());
        }
    }

    private void checkDistance(Section other) {
        if (distance <= other.distance) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_INSERT_SECTION_DISTANCE.getContent());
        }
    }

    public boolean hasStation(Long stationId) {
        return this.downStationId.equals(stationId) || upStationId.equals(stationId);
    }

    public Section merge(Section other) {
        checkStationsNotSame(other);
        int mergedDistance = distance + other.distance;

        if (upStationId.equals(other.downStationId)) {
            return new Section(id, line_id, other.upStationId, downStationId, mergedDistance);
        }
        if (downStationId.equals(other.upStationId)) {
            return new Section(id, line_id, upStationId, other.downStationId, mergedDistance);
        }
        throw new IllegalArgumentException(ExceptionMessage.NOT_CONNECTED_SECTIONS.getContent());
    }

    public boolean isUpperThan(Section other) {
        return downStationId.equals(other.upStationId);
    }

    public boolean isDownerThan(Section other) {
        return upStationId.equals(other.downStationId);
    }

    public Long getId() {
        return id;
    }

    public Long getLine_id() {
        return line_id;
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
