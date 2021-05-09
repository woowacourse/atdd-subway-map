package wooteco.subway.section;

import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

public class Section {

    private final long id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(long id, Line line, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }
}
