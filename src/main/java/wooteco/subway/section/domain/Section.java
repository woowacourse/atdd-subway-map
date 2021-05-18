package wooteco.subway.section.domain;

import java.util.Objects;
import wooteco.subway.domain.Id;
import wooteco.subway.exception.section.DuplicateStationException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.station.domain.Station;

public class Section {

    private final Id id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final Distance distance;

    public Section(final long key, final Section section) {
        this(new Id(key), section.line, section.upStation, section.downStation,
            section.distance);
    }

    public Section(final Line line, final Station upStation, final Station downStation,
        final int distance) {

        this(null, line, upStation, downStation,
            new Distance(distance));
    }

    public Section(final Long id, final Line line, final Station upStation,
        final Station downStation, final int distance) {

        this(new Id(id), line, upStation, downStation,
            new Distance(distance));
    }

    public Section(final Id id, final Line line, final Station upStation, final Station downStation,
        final Distance distance) {
        validateDuplicateStations(upStation, downStation);
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDuplicateStations(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new DuplicateStationException();
        }
    }

    public Section dividedSectionForSave(final Section section) {
        Distance updateDistance = this.distance.subtract(section.distance);
        if (upStation.equals(section.upStation)) {
            return new Section(null, line, section.downStation, downStation, updateDistance);
        }
        return new Section(null, line, upStation, section.upStation, updateDistance);
    }

    public Section assembledSectionForDelete(final Section downSection) {
        Distance updateDistance = downSection.distance.add(this.distance);
        return new Section(null, line, upStation, downSection.downStation, updateDistance);
    }

    public boolean isIncludeUpStation(Section section) {
        return this.getUpStation().equals(section.getUpStation()) ||
            this.getDownStation().equals(section.getUpStation());
    }

    public boolean isIncludeDownStation(Section section) {
        return this.getDownStation().equals(section.getDownStation()) ||
            this.getUpStation().equals(section.getDownStation());
    }

    public boolean isSameUpStation(Section section) {
        return this.getUpStation().equals(section.getUpStation());
    }

    public boolean isSameDownStation(Section section) {
        return this.getDownStation().equals(section.getDownStation());
    }

    public Long getId() {
        return id.value();
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Distance getDistance() {
        return distance;
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
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
