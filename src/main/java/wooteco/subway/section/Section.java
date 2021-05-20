package wooteco.subway.section;

import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

import java.util.ArrayList;
import java.util.List;

public class Section {

    private final long id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Section section, int distance) {
        this(-1, section.getLine(), section.getUpStation(), section.getDownStation(), distance);
    }

    public Section(long lineId, long upStationId, long downStationId) {
        this(-1, new Line(lineId), new Station(upStationId), new Station(downStationId), 0);
    }

    public Section(long lineId, SectionRequest sectionRequest) {
        this(-1,
                new Line(lineId),
                new Station(sectionRequest.getUpStationId()),
                new Station(sectionRequest.getDownStationId()),
                sectionRequest.getDistance());
    }

    public Section(Line line, Station upStation, Station downStation) {
        this(-1, line, upStation, downStation, -1);
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(-1, null, upStation, downStation, distance);
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this(-1, line, upStation, downStation, distance);
    }

    public Section(long id, Line line, Station upStation, Station downStation, int distance) {
        validateDistance(distance);
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance < 1) {
            throw new IllegalArgumentException("구간의 거리는 1보다 작을 수 없습니다.");
        }
    }

    public List<Section> update(Section newSection, Line line) {
        List<Section> sections = new ArrayList<>();
        if (upStation.getId().equals(newSection.getUpStation().getId())) {
            sections.add(newSection);
            sections.add(new Section(line, newSection.getDownStation(), downStation,
                    distance- newSection.getDistance()));
            return sections;
        }
        sections.add(new Section(line, upStation, newSection.getUpStation(),
                distance- newSection.getDistance()));
        sections.add(newSection);
        return sections;
    }

    public Section deleteStation(Section section) {
        if(downStation.getId() == section.getUpStation().getId()) {
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
