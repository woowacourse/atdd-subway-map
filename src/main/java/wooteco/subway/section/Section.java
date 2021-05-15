package wooteco.subway.section;

import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

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
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public int isAppropriateDistance(int beforeDistance) {
        if (beforeDistance - distance < 1) {
            throw new IllegalArgumentException("거리를 확인해주세요. 기존 거리보다 길거나 같을 수 없습니다.");
        }
        return beforeDistance - distance;
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
