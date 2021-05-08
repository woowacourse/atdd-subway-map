package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

import java.util.Objects;

public class Section {
    private Long id;
    private Line line;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section() {
    }

    public Section(final Line line, final Station upStation, final Station downStation, final int distance) {
        this(0L, line, upStation, downStation, distance);
    }

    public Section(final Long id, final Line line, final Station upStation, final Station downStation, final int distance) {
        this.id = id;
        changeLine(line);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long id() {
        return id;
    }

    public Line line() {
        return line;
    }

    public void changeLine(Line line) {
        this.line = line;
        line.getSections().add(this);
    }

    public Station upStation() {
        return upStation;
    }

    public void changeUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public Station downStation() {
        return downStation;
    }

    public void changeDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public int distance() {
        return distance;
    }

    public void changeDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }

    public boolean sameUpStation(Station targetStation) {
        return this.upStation.equals(targetStation);
    }

    public boolean sameDownStation(Station targetStation) {
        return this.downStation.equals(targetStation);
    }
}
