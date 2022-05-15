package wooteco.subway.domain;

import java.util.List;
import java.util.Objects;

public class Section {

    private static final int MIN_DISTANCE = 1;

    private Long id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

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

    public List<Section> split(Section other) {
        validateShortDistance(other.distance);
        if (upStation.equals(other.upStation)) {
            Section start = new Section(id, line, upStation, other.downStation, other.distance);
            Section end = new Section(line, other.downStation, downStation, distance - other.distance);
            return List.of(start, end);
        }
        if (downStation.equals(other.downStation)) {
            Section start = new Section(id, line, upStation, other.upStation, distance - other.distance);
            Section end = new Section(line, other.upStation, downStation, other.distance);
            return List.of(start, end);
        }
        return List.of(other);
    }

    public boolean isEqualToLine(Line line) {
        return this.line.equals(line);
    }

    public boolean isEqualToUpOrDownStation(Station station) {
        return isEqualToUpStation(station) || isEqualToDownStation(station);
    }

    public boolean isEqualToUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isEqualToDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean canConnectToNext(Section other) {
        return downStation.equals(other.upStation);
    }

    private void validate(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException("거리는 1이상이어야 합니다.");
        }
    }

    private void validateShortDistance(int distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException("추가하려는 거리가 큽니다.");
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
        if (!(o instanceof Section)) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(upStation, section.upStation) && Objects
            .equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation);
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", line=" + line +
            ", upStation=" + upStation +
            ", downStation=" + downStation +
            ", distance=" + distance +
            '}';
    }
}
