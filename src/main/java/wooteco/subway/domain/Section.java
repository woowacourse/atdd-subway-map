package wooteco.subway.domain;

import wooteco.subway.exception.section.SectionDistanceMismatchException;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Distance distance;

    public Section() {

    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(lineId, upStationId, downStationId, new Distance(distance));
    }

    public Section(Long lineId, Long upStationId, Long downStationId, Distance distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = new Distance(distance);
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, Distance distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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
        return distance.getDistance();
    }

    public boolean isEqualUpStationId(Section section) {
        return upStationId.equals(section.getUpStationId());
    }

    public boolean isLongerDistanceThan(Section section) {
        return distance.isLongerDistanceThan(section);
    }

    public int calculateMaxDistance(Section insertSection) {
        return distance.calculateMax(new Distance(insertSection.getDistance()));
    }

    public int calculateMinDistance(Section insertSection) {
        return distance.calculateMin(new Distance(insertSection.getDistance()));
    }

    public Section makeSectionsToStraight(Section insertSection) {
        validateCanBeInsertedByDistance(insertSection);
        if (isEqualUpStationId(insertSection)) {
            return new Section(
                    id,
                    lineId,
                    insertSection.getDownStationId(),
                    downStationId,
                    updateDistance(this, insertSection)
            );
        }

        return new Section(
                id,
                lineId,
                insertSection.getUpStationId(),
                upStationId,
                updateDistance(this, insertSection)
        );
    }

    private void validateCanBeInsertedByDistance(Section insertSection) {
        if (!isLongerDistanceThan(insertSection)) {
            throw new SectionDistanceMismatchException();
        }
    }

    public int updateDistance(Section section, Section insertSection) {
        final int maxDistance = section.calculateMaxDistance(insertSection);
        final int minDistance = section.calculateMinDistance(insertSection);
        return maxDistance - minDistance;
    }

    public boolean isSameBetweenUpAndDownStation() {
        return upStationId.equals(downStationId);
    }

    public boolean isDistanceMoreThanZero() {
        return distance.isMoreThanZero();
    }
}
