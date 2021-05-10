package wooteco.subway.domain;

import org.springframework.lang.NonNull;

public class Section {

    private final Long id;
    @NonNull
    private final Line line;
    @NonNull
    private final Station upStation;
    @NonNull
    private final Station downStation;
    @NonNull
    private final Distance distance;

    public Section(Long id, Line line, Station upStation, Station downStation,
        Distance distance) {
        validateUpStationAndDownStation(upStation, downStation);
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Line line, Station upStation, Station downStation,
        Distance distance) {
        this(null, line, upStation, downStation, distance);
    }

    public Section(Long id, Section section) {
        this(id, section.getLine(), section.getUpStation(), section.getDownStation(),
            section.getDistance());
    }

    private static void validateUpStationAndDownStation(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행역과 하행역이 같을 수 없습니다.");
        }
    }

    public Station sameStation(Section section) {
        if (upStation.equals(section.getUpStation())) {
            return upStation;
        }
        if (downStation.equals(section.getDownStation())) {
            return downStation;
        }
        throw new IllegalArgumentException("같은 역이 존재하지 않습니다.");
    }

    public boolean hasStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public boolean hasOnlyOneSameStation(Section section) {
        boolean isSameUpStation = upStation.equals(section.getUpStation());
        boolean isSameDownStation = downStation.equals(section.getDownStation());

        return (isSameUpStation && !isSameDownStation) || (!isSameUpStation && isSameDownStation);
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

    public Distance getDistance() {
        return distance;
    }
}
