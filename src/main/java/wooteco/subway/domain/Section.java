package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.invalidrequest.InvalidSectionCreateRequestException;

public class Section {

    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(Long id, Station upStation, Station downStation, int distance) {
        validateEndpoints(upStation, downStation);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateEndpoints(Station upStation, Station downStation) {
        if (upStation.hasSameName(downStation)) {
            throw new InvalidSectionCreateRequestException("구간의 시작과 끝은 같은 역일 수 없습니다.");
        }
    }

    public boolean hasSameStations(Section section) {
        return isSame(section) || isReversed(section);
    }

    private boolean isSame(Section section) {
        return upStation.hasSameName(section.upStation) && downStation.hasSameName(section.downStation);
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

    public boolean containStation(Station station) {
        return upStation.hasSameName(station) || downStation.hasSameName(station);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(
                upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
    }

}
