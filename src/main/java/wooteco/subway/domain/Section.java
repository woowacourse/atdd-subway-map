package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Station upStation, Station downStation, int distance) {
        Objects.requireNonNull(upStation);
        Objects.requireNonNull(downStation);
        validateDistanceOverZero(distance);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDistanceOverZero(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("거리는 0 이하가 될 수 없습니다.");
        }
    }

    public boolean haveAnyStation(Section section) {
        return haveUpStation(section) || haveDownStation(section);
    }

    public boolean isSameUpOrDownStation(Section section) {
        return upStation.equals(section.upStation) || downStation.equals(section.downStation);
    }

    public boolean haveUpStation(Section section) {
        return upStation.equals(section.upStation) || downStation.equals(section.upStation);
    }

    public boolean haveDownStation(Section section) {
        return upStation.equals(section.downStation) || downStation.equals(section.downStation);
    }

    public boolean isShortAndEqualDistanceThan(Section section) {
        return distance <= section.distance;
    }

    public Section slice(Section insertSection) {
        if (downStation.equals(insertSection.downStation)) {
            return new Section(
                    upStation,
                    insertSection.upStation,
                    distance - insertSection.distance
            );
        }
        return new Section(
                insertSection.downStation,
                downStation,
                distance - insertSection.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(upStation, section.upStation)
                && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
