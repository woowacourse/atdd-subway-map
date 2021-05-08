package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

public class Section {
    private Long id;
    private Line line;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(){}

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
}
