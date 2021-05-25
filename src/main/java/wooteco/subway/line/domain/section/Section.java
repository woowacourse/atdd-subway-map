package wooteco.subway.line.domain.section;

import java.util.Objects;

public class Section {
    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final Distance distance;

    public Section(Long lineId, Long upStationId, Long downStationId, Distance distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, Distance distance) {
        validateSection(lineId, upStationId, downStationId, distance);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateSection(Long lineId, Long upStationId, Long downStationId, Distance distance) {
        validateNull(lineId, upStationId, downStationId, distance);
    }

    private void validateNull(Long lineId, Long upStationId, Long downStationId, Distance distance) {
        Objects.requireNonNull(lineId, "lineId는 null이 될 수 없습니다.");
        Objects.requireNonNull(upStationId, "upStationId는 null이 될 수 없습니다.");
        Objects.requireNonNull(downStationId, "downStationId는 null이 될 수 없습니다.");
        Objects.requireNonNull(distance, "distance는 null이 될 수 없습니다.");
    }

    public boolean hasUpStationIdOrDownStationId(Section section) {
        return this.isSameUpStationId(section) || this.isSameDownStationId(section);
    }

    public boolean hasStationId(Long stationsId) {
        return this.downStationId.equals(stationsId) || this.upStationId.equals(stationsId);
    }

    public boolean isSameUpStationId(Section section) {
        return this.upStationId.equals(section.upStationId);
    }

    public boolean isSameDownStationId(Section section) {
        return this.downStationId.equals(section.downStationId);
    }

    public void validateAddableDistance(Section newSection) {
        if (this.distance.lessThanOrEqualTo(newSection.distance)) {
            throw new IllegalArgumentException("역과 역 사이 새로운 역을 추가할 때 기존 역 사이의 길이보다 크거나 같으면 등록할 수 없습니다.");
        }
    }

    public Distance distanceDifference(Section section) {
        return this.distance.minus(section.distance);
    }

    public Section mergeWithoutDuplicateStationId(Section section) {
        if (this.upStationId.equals(section.downStationId)) {
            return new Section(lineId, section.upStationId, this.downStationId, this.distance.plus(section.distance));
        }
        return new Section(lineId, section.downStationId, this.upStationId, this.distance.plus(section.distance));
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

    public Distance getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(lineId, section.lineId) && Objects.equals(upStationId, section.upStationId)
                && Objects.equals(downStationId, section.downStationId) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStationId, downStationId, distance);
    }
}
