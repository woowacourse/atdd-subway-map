package wooteco.subway.domain;

import java.util.Objects;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    private Section() {
    }

    public Section(Long upStationId, Long downStationId, int distance) {
        validateSection(upStationId, downStationId, distance);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(upStationId, downStationId, distance);
        this.lineId = lineId;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this(lineId, upStationId, downStationId, distance);
        this.id = id;
    }

    private void validateSection(Long upStationId, Long downStationId, int distance) {
        if (Objects.equals(upStationId, downStationId)) {
            throw new IllegalArgumentException("상행역과 하행역은 같을 수 없습니다.");
        }
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 거리는 0 이하일 수 없습니다.");
        }
    }

    public boolean hasSameUpStation(Section section) {
        return Objects.equals(upStationId, section.upStationId);
    }

    public boolean hasSameDownStation(Section section) {
        return Objects.equals(downStationId, section.downStationId);
    }

    public boolean isLongerThan(Section section) {
        return distance > section.distance;
    }

    public boolean hasSameUpStationWithOtherDownStation(Section section) {
        return Objects.equals(upStationId, section.downStationId);
    }

    public boolean hasSameDownStationWithOtherUpStation(Section section) {
        return Objects.equals(downStationId, section.upStationId);
    }

    public boolean isSameStations(Section section) {
        return Objects.equals(upStationId, section.upStationId) &&
                Objects.equals(downStationId, section.downStationId);
    }

    public boolean isNotSameAnyStation(Section section) {
        return !(Objects.equals(upStationId, section.upStationId)) &&
                !(Objects.equals(downStationId, section.downStationId));
    }

    public Section splitSectionBySameUpStation(Section shorterSection) {
        return new Section(shorterSection.downStationId, downStationId, distance - shorterSection.distance);
    }

    public Section splitSectionBySameDownStation(Section shorterSection) {
        return new Section(upStationId, shorterSection.upStationId, distance - shorterSection.distance);
    }

    public Section mergeSectionByCut(Section downSection) {
        return new Section(upStationId, downSection.downStationId, distance + downSection.distance);
    }

    public boolean hasSameUpStationByStation(Station station) {
        return station.isSameStation(upStationId);
    }

    public boolean hasSameDownStationByStation(Station station) {
        return station.isSameStation(downStationId);
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

    public void setUpStationId(Long upStationId) {
        this.upStationId = upStationId;
    }

    public void setDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(lineId, section.lineId) && Objects.equals(upStationId, section.upStationId)
                && Objects.equals(downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStationId, downStationId);
    }
}
