package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Section {
    private final long id;
    private final long lineId;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(long lineId, String upStation, String downStation, int distance) {
        this(0L, lineId, new Station(upStation), new Station(downStation), distance);
    }

    public Section(long lineId, Station upStation, Station downStation, int distance) {
        this(0L, lineId, upStation, downStation, distance);
    }

    public Section(long id, long lineId, Station upStation, Station downStation, int distance) {
        this.lineId = lineId;
        validateSection(upStation, downStation, distance);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateSection(Station upStation, Station downStation, int distance) {
        checkWhetherStationsAreDifferent(upStation, downStation);
        checkValidDistance(distance);
    }

    private void checkWhetherStationsAreDifferent(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행 종점과 하행 종점이 같을 수 없습니다.");
        }
    }

    private void checkValidDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("두 종점간의 거리는 0보다 커야합니다.");
        }
    }

    public long getId() {
        return id;
    }

    public long getLineId() {
        return lineId;
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section)o;
        return lineId == section.lineId && distance == section.distance && Objects.equals(upStation,
                section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStation, downStation, distance);
    }

    public List<Section> splitSectionIfSameUpStation(Station downStation, int distance) {
        List<Section> sections = new ArrayList<>();
        sections.add(new Section(this.lineId, this.upStation, downStation, distance));
        sections.add(new Section(this.lineId, downStation, this.downStation, this.distance - distance));
        return sections;
    }

    public List<Section> splitSectionIfSameDownStation(Station upStation, int distance) {
        List<Section> sections = new ArrayList<>();
        sections.add(new Section(this.lineId, upStation, this.downStation, distance));
        sections.add(new Section(this.lineId, this.upStation, upStation, this.distance - distance));
        return sections;
    }
}
