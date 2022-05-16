package wooteco.subway.domain;

import java.util.List;
import java.util.Objects;

public class Section {

    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final Line line;
    private final int distance;

    public Section(Long id, Station upStation, Station downStation, Line line, int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = distance;
        validateField();
    }

    public Section(Long id, Section section) {
        this(id, section.upStation, section.downStation, section.line, section.distance);
    }

    public Section(Station upStation, Station downStation, Line line, int distance) {
        this(0L, upStation, downStation, line, distance);
    }

    private void validateField() {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("구간에서 상행선과 하행선은 같은 역으로 할 수 없습니다.");
        }

        if (distance < 1) {
            throw new IllegalArgumentException("상행선과 하행선의 거리는 1 이상이어야 합니다.");
        }
    }

    public boolean beIncludedInUpStation(List<Station> stations) {
        return stations.contains(upStation);
    }

    public boolean beIncludedInDownStation(List<Station> stations) {
        return stations.contains(downStation);
    }

    public boolean isEqualOfUpStation(Section section) {
        return upStation.equals(section.upStation);
    }

    public boolean isEqualOfUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isEqualOfDownStation(Section section) {
        return downStation.equals(section.downStation);
    }

    public boolean isEqualOfDownStation(Station station) {
        return downStation.equals(station);
    }

    public Section getCutDistanceSection(Section section) {
        int cutDistance = this.distance - section.distance;
        if(cutDistance <= 0) {
            throw new IllegalArgumentException("이미 존재하는 구간의 거리보다 거리가 길거나 같습니다.");
        }
        if (upStation.equals(section.upStation)) {
            return new Section(section.downStation, downStation, line, cutDistance);
        }
        return new Section(upStation, section.upStation, line, cutDistance);
    }

    public boolean containsStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Long getDownStationId() {
        return downStation.getId();
    }

    public Long getLineId() {
        return line.getId();
    }

    public int getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
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
        return distance == section.distance && Objects.equals(upStation, section.upStation)
                && Objects.equals(downStation, section.downStation) && Objects.equals(line,
                section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation, line, distance);
    }
}
