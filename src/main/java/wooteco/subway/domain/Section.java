package wooteco.subway.domain;

import java.util.List;
import java.util.Objects;

public class Section {

    public static final String DISTANCE_NEGATIVE_ERROR_MESSAGE = "거리는 1이상이어야 합니다.";
    public static final String DISTANCE_OVER_ERROR_MESSAGE = "추가하려는 구간의 거리가 현재 구간의 거리리보다 큽니다.";
    private static final int MIN_DISTANCE = 1;

    private Long id;
    private Line line;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        validate(distance);
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this(null, line, upStation, downStation, distance);
    }

    public List<Section> splitFromUpStation(Section other) {
        validateShortDistance(other.distance);
        Section start = new Section(id, line, upStation, other.downStation, other.distance);
        Section end = new Section(line, other.downStation, downStation, distance - other.distance);
        return List.of(start, end);
    }

    public List<Section> splitFromDownStation(Section other) {
        validateShortDistance(other.distance);
        Section start = new Section(id, line, upStation, other.upStation, distance - other.distance);
        Section end = new Section(line, other.upStation, downStation, other.distance);
        return List.of(start, end);
    }

    public boolean hasSameUpStation(Station other) {
        return upStation.equals(other);
    }

    public boolean hasSameDownStation(Station other) {
        return downStation.equals(other);
    }

    private void validate(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException(DISTANCE_NEGATIVE_ERROR_MESSAGE);
        }
    }

    private void validateShortDistance(int distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException(DISTANCE_OVER_ERROR_MESSAGE);
        }
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
