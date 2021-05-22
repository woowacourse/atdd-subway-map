package wooteco.subway.section;

import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

import java.util.ArrayList;
import java.util.List;

public class Section {

    private final Long id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final Integer distance;

    public Section(Section section, int distance) {
        this(null, section.getLine(), section.getUpStation(), section.getDownStation(), distance);
    }

    public Section(long lineId, long upStationId, long downStationId) {
        this(null, new Line(lineId), new Station(upStationId), new Station(downStationId), null);
    }

    public Section(long lineId, SectionRequest sectionRequest) {
        this(null,
                new Line(lineId),
                new Station(sectionRequest.getUpStationId()),
                new Station(sectionRequest.getDownStationId()),
                sectionRequest.getDistance());
    }

    public Section(Line line, Station upStation, Station downStation) {
        this(null, line, upStation, downStation, null);
    }

    public Section(Station upStation, Station downStation, Integer distance) {
        this(null, null, upStation, downStation, distance);
    }

    public Section(Line line, Station upStation, Station downStation, Integer distance) {
        this(null, line, upStation, downStation, distance);
    }

    public Section(Long id, Line line, Station upStation, Station downStation, Integer distance) {
        validateDistance(distance);
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance == 0) {
            throw new IllegalArgumentException("구간의 거리는 1보다 작을 수 없습니다.");
        }
    }

    public List<Section> update(Section newSection) {
        List<Section> sections = new ArrayList<>();
        if (upStation.isSameStation(newSection.getUpStation())) {
            sections.add(newSection);
            sections.add(new Section(line, newSection.getDownStation(), downStation,
                    distance - newSection.getDistance()));
            return sections;
        }
        sections.add(new Section(line, upStation, newSection.getUpStation(),
                distance - newSection.getDistance()));
        sections.add(newSection);
        return sections;
    }

    public Section deleteStation(Section section) {
        if (downStation.isSameStation(section.getUpStation())) {
            return new Section(line, upStation, section.getDownStation(), distance + section.getDistance());
        }
        return new Section(line, section.getUpStation(), downStation, distance + section.getDistance());
    }

    public long getId() {
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

}
