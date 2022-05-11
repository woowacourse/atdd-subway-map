package wooteco.subway.domain.line;

import java.util.List;

import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;

public class Line {

    private static final long TEMPORARY_ID = 0L;

    private final Long id;
    private final Sections sections;
    private LineName name;
    private LineColor color;

    public Line(Long id, Sections sections, String name, String color) {
        this.id = id;
        this.sections = sections;
        this.name = new LineName(name);
        this.color = new LineColor(color);
    }

    public Line(Long id, List<Section> sections, String name, String color) {
        this(id, new Sections(sections), name, color);
    }
    public Line(Sections sections, String name, String color) {
        this(TEMPORARY_ID, sections, name, color);
    }

    public Line(List<Section> sections, String name, String color) {
        this(TEMPORARY_ID, sections, name, color);
    }

    public void update(String name, String color) {
        this.name = new LineName(name);
        this.color = new LineColor(color);
    }

    public Long getId() {
        return id;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public List<Long> getSectionIds() {
        return sections.getSectionIds();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public String getName() {
        return name.getName();
    }

    public String getColor() {
        return color.getColor();
    }
}
