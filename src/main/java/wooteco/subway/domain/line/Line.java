package wooteco.subway.domain.line;

import java.util.List;
import java.util.Objects;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;

public class Line {

    private final LineInfo lineInfo;
    private final Sections sections;

    public Line(LineInfo lineInfo, Sections sections) {
        this.lineInfo = lineInfo;
        this.sections = sections;
    }

    public static Line of(LineInfo lineInfo, Section section) {
        return new Line(lineInfo, new Sections(List.of(section)));
    }

    public Long getId() {
        return lineInfo.getId();
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }

    public Sections getSections() {
        return sections;
    }

    public List<Station> getSortedStations() {
        return sections.toSortedStations();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(lineInfo, line.lineInfo)
                && Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineInfo, sections);
    }

    @Override
    public String toString() {
        return "Line{" + "lineInfo=" + lineInfo + ", sections=" + sections + '}';
    }
}
