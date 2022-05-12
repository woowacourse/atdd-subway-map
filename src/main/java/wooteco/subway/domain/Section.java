package wooteco.subway.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Section {

    private static final int MIN_DISTANCE = 1;

    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;
    private Long id;

    public Section(final Long id, final Long lineId, final Long upStationId, final Long downStationId,
                   final int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        validate(upStationId, downStationId, distance);
        this.lineId = Objects.requireNonNull(lineId);
        this.upStationId = Objects.requireNonNull(upStationId);
        this.downStationId = Objects.requireNonNull(downStationId);
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

    public List<Section> assign(final Section newSection) {
        checkBetweenDistance(newSection.distance);

        final int assignedDistance = this.distance - newSection.distance;
        if (upStationId.equals(newSection.upStationId)) {
            return List.of(
                    newSection,
                    new Section(lineId, newSection.downStationId, downStationId, assignedDistance)
            );
        }
        return List.of(
                new Section(lineId, upStationId, newSection.upStationId, assignedDistance),
                newSection
        );
    }

    private void checkBetweenDistance(final int newSectionDistance) {
        if (distance <= newSectionDistance) {
            throw new IllegalArgumentException("기존 구간의 길이 보다 작지 않습니다.");
        }
    }

    public Section merge(final Section section) {
        final Long criteriaId = findDuplicateId(section);
        final int mergedDistance = distance + section.getDistance();
        if (criteriaId.equals(upStationId)) {
            return new Section(
                    lineId,
                    section.upStationId,
                    downStationId,
                    mergedDistance
            );
        }
        return new Section(
                lineId,
                upStationId,
                section.downStationId,
                mergedDistance
        );
    }

    private Long findDuplicateId(final Section section) {
        final Set<Long> stationIds = new HashSet<>();
        stationIds.add(upStationId);
        stationIds.add(downStationId);
        if (stationIds.contains(section.upStationId)) {
            return section.upStationId;
        }
        return section.downStationId;
    }

    public boolean contains(final Long stationId) {
        return upStationId.equals(stationId) || downStationId.equals(stationId);
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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Section section = (Section) o;
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
                "lineId=" + lineId +
                ", upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                ", id=" + id +
                '}';
    }
}
