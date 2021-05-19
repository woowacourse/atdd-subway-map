package wooteco.subway.domain.line;

import wooteco.subway.domain.line.section.Section;
import wooteco.subway.domain.line.section.Sections;
import wooteco.subway.domain.line.value.line.LineColor;
import wooteco.subway.domain.line.value.line.LineId;
import wooteco.subway.domain.line.value.line.LineName;

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
        this(LineId.empty(), lineName, lineColor, sections);
    }

    public Line(LineName lineName, LineColor lineColor) {
        this(LineId.empty(), lineName, lineColor, Sections.empty());
    }

    public Line(LineId lineId, LineName lineName, LineColor lineColor) {
        this(lineId, lineName, lineColor, Sections.empty());
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

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Long> getStationIds() {
        return sections.getStationIds();
    }

    public List<Long> getUnorderedStationIds() {
        return sections.getUnOrderedStationIds();
    }

    public void deleteSectionByStationId(Long stationId) {
        sections.deleteSectionByStationId(stationId);
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
