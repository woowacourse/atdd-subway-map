package wooteco.subway.section;

import java.util.ArrayDeque;
import java.util.Deque;
import wooteco.subway.exception.SameStationIdException;
import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

public class Section {

    private Long id;
    private Line line;
    private Station upStation;
    private Station downStation;
    private int distance;

    private Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this(null, line, upStation, downStation, distance);
    }

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        validateDifferentStations(upStation, downStation);
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean largerThan(int distance) {
        return this.distance >= distance;
    }

    public boolean isEndPointOf(Sections sections) {
        Deque<Station> stations = new ArrayDeque<>(sections.sortedStations());
        return stations.peekFirst().equals(downStation) || stations.peekLast().equals(upStation);
    }

    private void validateDifferentStations(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new SameStationIdException();
        }
    }

    public boolean isUpStation(Station upStation) {
        return this.upStation.equals(upStation);
    }

    public int deductDistance(Section newSection) {
        return this.distance - newSection.distance;
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

    public Long getId() {
        return id;
    }
}
