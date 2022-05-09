package wooteco.subway.domain;

public class Section {

    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final Integer distance;

    public Section(final Long id,
                   final Station upStation,
                   final Station downStation,
                   final Integer distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section createWithoutId(final Station upStation,
                                          final Station downStation,
                                          final Integer distance) {
        return new Section(null, upStation, downStation, distance);
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

    public Integer getDistance() {
        return distance;
    }
}
