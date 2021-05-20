package wooteco.subway.line.domain;

import wooteco.subway.common.exception.InvalidInputException;
import wooteco.subway.station.domain.Station;

import java.util.Objects;

public class Section {
    private Long id;
    private Line line;
    private Station upStation;
    private Station downStation;
    private int distance;

    public  Section() {
    }

    public Section(final Station upStation, final Station downStation, final int distance) {
        this(null, null, upStation, downStation, distance);
    }

    public Section(final Line line, final Station upStation, final Station downStation, final int distance) {
        this(null, line, upStation, downStation, distance);
    }

    public Section(final Long id, final Line line, final Station upStation, final Station downStation, final int distance) {
        this.id = id;
        this.line = line;
        validateStation(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long id() {
        return id;
    }

    public Line line() {
        return line;
    }

    public Station upStation() {
        return upStation;
    }

    public Station downStation() {
        return downStation;
    }

    public int distance() {
        return distance;
    }

    public boolean sameUpStation(final Station targetStation) {
        return this.upStation.equals(targetStation);
    }

    public boolean sameDownStation(final Station targetStation) {
        return this.downStation.equals(targetStation);
    }

    private void validateStation(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new InvalidInputException("상행역과 하행역은 같을 수 없음! ");
        }
    }

    public long lineId() {
        return this.line.id();
    }

    public long upStationId() {
        return this.upStation.id();
    }

    public long downStationId() {
        return this.downStation.id();
    }

    public boolean same(final Section section) {
        return this.equals(section);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id)
                && Objects.equals(line, section.line)
                && Objects.equals(upStation, section.upStation)
                && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
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
