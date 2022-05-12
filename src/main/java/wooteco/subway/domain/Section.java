package wooteco.subway.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final Distance distance;

    public Section(final Long id, final Long lineId, final Long upStationId, final Long downStationId,
                   final Distance distance) {
        validate(upStationId, downStationId);
        this.id = id;
        this.lineId = Objects.requireNonNull(lineId);
        this.upStationId = Objects.requireNonNull(upStationId);
        this.downStationId = Objects.requireNonNull(downStationId);
        this.distance = distance;
    }

    public Section(final Long id, final Long lineId, final Long upStationId, final Long downStationId,
                   final int distance) {
        this(id, lineId, upStationId, downStationId, new Distance(distance));
    }

    public Section(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(final Long lineId, final Long upStationId, final Long downStationId, final Distance distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    private void validate(final Long upStationId, final Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("두 종점이 동일합니다.");
        }
    }

    public List<Section> assign(final Section newSection) {
        distance.checkCanAssign(newSection.distance);

        final Distance assignedDistance = distance.minus(newSection.distance);
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

    public Section merge(final Section section) {
        final Long criteriaId = findDuplicateId(section);
        final Distance mergedDistance = distance.plus(section.distance);
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

    public boolean hasSameUpStationId(final Long upStationId) {
        return this.upStationId.equals(upStationId);
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
        return distance.getValue();
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
        return Objects.equals(lineId, section.lineId) && Objects.equals(upStationId,
                section.upStationId) && Objects.equals(downStationId, section.downStationId)
                && Objects.equals(distance, section.distance) && Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStationId, downStationId, distance, id);
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
