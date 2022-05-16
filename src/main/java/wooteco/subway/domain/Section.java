package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private static final int MINIMUM_DISTANCE = 0;

    private Long sectionId;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(final Long sectionId, final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        validateDistance(distance);
        this.sectionId = sectionId;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(final Long upStationId, final Long downStationId, final int distance) {
        this(null, null, upStationId, downStationId, distance);
    }

    public static Section replaced(final Section existSection, final Section section) {
        int newDistance = subtractDistance(existSection, section);
        if (existSection.upStationId.equals(section.upStationId)) {
            return new Section(existSection.sectionId, existSection.lineId,
                    section.downStationId, existSection.downStationId, newDistance);
        }
        return new Section(existSection.upStationId, existSection.lineId,
                existSection.upStationId, section.upStationId, newDistance);
    }

    public static Section deleted(final Section sectionIncludedDownStation, final Section sectionIncludedUpStation) {
        return new Section(
                sectionIncludedDownStation.sectionId,
                sectionIncludedDownStation.lineId,
                sectionIncludedDownStation.upStationId,
                sectionIncludedUpStation.downStationId,
                sectionIncludedUpStation.distance + sectionIncludedDownStation.distance
        );
    }

    public static int subtractDistance(final Section existSection, final Section section) {
        int distance = existSection.distance - section.distance;
        if (distance <= MINIMUM_DISTANCE) {
            throw new IllegalArgumentException("기존 구간의 길이를 벗어납니다.");
        }
        return distance;
    }

    private void validateDistance(final int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 거리는 1 이상이어야 합니다.");
        }
    }

    public boolean existStation(final Long stationId) {
        return upStationId.equals(stationId) || downStationId.equals(stationId);
    }

    public boolean isAddingEndSection(final Section section) {
        return upStationId.equals(section.downStationId)
                || downStationId.equals(section.upStationId);
    }

    public boolean hasUpStation(final Long sectionId) {
        return upStationId.equals(sectionId);
    }

    public boolean hasDownStation(final Long sectionId) {
        return downStationId.equals(sectionId);
    }

    public Long getSectionId() {
        return sectionId;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(upStationId, section.upStationId)
                && Objects.equals(downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, distance);
    }
}
