package wooteco.subway.domain;

import java.util.stream.Stream;

public class Section {

    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(Long id, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean hasSameStations(Section section) {
        return Stream.of(upStation, downStation)
                .anyMatch(station -> station.hasSameName(section.upStation) && station.hasSameName(
                        section.downStation)) || isReversed(section);
    }
    
    private boolean isReversed(Section section) {
        return upStation.hasSameName(section.downStation) && downStation.hasSameName(section.upStation);
    }

    public boolean hasSameUpStation(Section section) {
        return this.upStation.hasSameName(section.getUpStation());
    }

    public boolean hasSameDownStation(Section section) {
        return this.downStation.hasSameName(section.getDownStation());
    }

    public boolean canInclude(Section section) {
        return this.distance - section.distance > 0;
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

    public int getDistance() {
        return distance;
    }
}
