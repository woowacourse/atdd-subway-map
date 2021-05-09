package wooteco.subway.line.domain;

import wooteco.subway.line.domain.section.Section;
import wooteco.subway.line.domain.section.Sections;
import wooteco.subway.line.domain.value.LineColor;
import wooteco.subway.line.domain.value.LineId;
import wooteco.subway.line.domain.value.LineName;

import java.util.List;
import java.util.Objects;

public class Line {
    private final LineId lineId;
    private final LineName lineName;
    private final LineColor lineColor;
    private final Sections sections;

    public Line(LineId lineId, LineName lineName, LineColor lineColor, Sections sections) {
        this.lineId = lineId;
        this.lineName = lineName;
        this.lineColor = lineColor;
        this.sections = sections;
    }

    public Line(LineName lineName, LineColor lineColor, Sections sections) {
        this(null, lineName, lineColor, sections);
    }

    public Line(LineId lineId, LineName lineName, LineColor lineColor) {
        this(lineId, lineName, lineColor, null);
    }

    public Long getLineId() {
        return lineId.longValue();
    }

    public String getLineName() {
        return lineName.asString();
    }

    public String getLineColor() {
        return lineColor.asString();
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(lineId, line.lineId) && Objects.equals(lineName, line.lineName) && Objects.equals(lineColor, line.lineColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, lineName, lineColor);
    }

}
