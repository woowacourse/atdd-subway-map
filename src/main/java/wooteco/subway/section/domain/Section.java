package wooteco.subway.section.domain;

import java.util.Objects;
import wooteco.subway.exception.SectionDistanceException;
import wooteco.subway.station.domain.Station;

public class Section {

    private static final int DISTANCE_MIN = 1;
    private Long id;
    private final Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Long lineId, Station upStation, Station downStation, int distance) {
        validateDistance(distance);
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Station upStation,
        Station downStation, int distance) {
        this(lineId, upStation, downStation, distance);
        this.id = id;
    }

    private void validateDistance(int distance) {
        if (distance < DISTANCE_MIN) {
            throw new SectionDistanceException();
        }
    }

    public boolean isSameUpStation(Section section) {
        return upStation.equals(section.getUpStation());
    }

    public boolean isSameDownStation(Section section) {
        return downStation.equals(section.getDownStation());
    }

    public int minusDistance(Section section) {
        return distance - section.getDistance();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Long getDownStationId() {
        return downStation.getId();
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
        return distance == section.distance && Objects.equals(id, section.id)
            && Objects.equals(lineId, section.lineId) && Objects
            .equals(upStation, section.upStation) && Objects
            .equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStation, downStation, distance);
    }
}
