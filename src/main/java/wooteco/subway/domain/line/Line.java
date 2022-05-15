package wooteco.subway.domain.line;

import java.util.List;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;

public class Line {

    private final LineInfo lineInfo;
    private final Sections sections;

    public Line(LineInfo lineInfo, Sections sections) {
        this.lineInfo = lineInfo;
        this.sections = sections;
    }

    public Long getId() {
        return lineInfo.getId();
    }

    public String getName() {
        return lineInfo.getName();
    }

    public String getColor() {
        return lineInfo.getColor();
    }

    public List<Station> getStations() {
        return sections.toSortedStations();
    }
}
