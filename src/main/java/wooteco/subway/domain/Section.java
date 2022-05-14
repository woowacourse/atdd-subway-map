package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private final Long id;
    private final Line line;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this(null, line, upStation, downStation, distance);
    }

    public boolean existsStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public boolean isSameUpAndDownStation(Section section) {
        return isSameUpStation(section) && isSameDownStation(section);
    }

    public boolean isSameUpStation(Section section) {
        return upStation.equals(section.upStation);
    }

    public boolean isSameDownStation(Section section) {
        return downStation.equals(section.downStation);
    }

    public boolean isConnect(Section section) {
        return downStation.equals(section.upStation);
    }

    public void changeUpStation(Section section) {
        upStation = section.downStation;
        distance -= section.distance;
    }

    public void changeDownStation(Section section) {
        downStation = section.downStation;
        distance -= section.distance;
    }

    public Long getId() {
        return id;
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

    public int getDistance() {
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
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(line,
                section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(
                downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}
