package wooteco.subway.domain;

import wooteco.subway.exception.IllegalDistanceException;

public class Section {

    private final Long id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(final Long id,
                   final Line line,
                   final Station upStation,
                   final Station downStation,
                   final int distance) {
        validateDistance(distance);
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDistance(final int distance) {
        if (distance <= 0) {
            throw new IllegalDistanceException();
        }
    }

    public Section(final Line line,
                   final Station upStation,
                   final Station downStation,
                   final int distance) {
        this(null, line, upStation, downStation, distance);
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
}
