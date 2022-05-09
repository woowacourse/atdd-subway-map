package wooteco.subway.domain;

public class Section {

    private final Long id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final Integer distance;

    public Section(final Long id,
                   final Line line,
                   final Station upStation,
                   final Station downStation,
                   final Integer distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section createWithoutId(final Line line,
                                          final Station upStation,
                                          final Station downStation,
                                          final Integer distance) {
        return new Section(null, line, upStation, downStation, distance);
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

    public Integer getDistance() {
        return distance;
    }
}
