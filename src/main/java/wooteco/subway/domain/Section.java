package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;
    private final Long lineId;

    public Section(final Long id, final Station upStation, final Station downStation, final int distance, final Long lineId) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.lineId = lineId;
    }

    public Section(final Station upStation, final Station downStation, final int distance, final Long lineId) {
        this(null, upStation, downStation, distance, lineId);
    }

    public Section(final Station upStation, final Station downStation, final int distance) {
        this(null, upStation, downStation, distance, null);
    }

    public boolean isGreaterOrEqualTo(final Section other) {
        return this.distance >= other.distance;
    }

    public Section createSectionByUpStation(final Section section) {
        return new Section(this.id, this.upStation, section.upStation, calculateDistance(section), this.lineId);
    }

    public Section createSectionByDownStation(final Section section) {
        return new Section(this.id, section.downStation, this.downStation, calculateDistance(section), this.lineId);
    }

    public Section createSectionInBetween(Section section) {
        if (this.upStation.equals(section.upStation)) {
            return createSectionByDownStation(section);
        }
        return createSectionByUpStation(section);
    }

    public Section merge(final Section section) {
        return new Section(this.upStation, section.downStation, mergeDistance(section), this.lineId);
    }

    private int calculateDistance(final Section section) {
        return this.distance - section.distance;
    }

    private int mergeDistance(final Section section) {
        return this.distance + section.distance;
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        final Section section = (Section) o;
        return distance == section.distance &&
                Objects.equals(id, section.id) &&
                Objects.equals(upStation, section.upStation) &&
                Objects.equals(downStation, section.downStation) &&
                Objects.equals(lineId, section.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance, lineId);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                ", lineId=" + lineId +
                '}';
    }
}
