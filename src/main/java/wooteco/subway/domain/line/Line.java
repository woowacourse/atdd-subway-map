package wooteco.subway.domain.line;

import java.util.ArrayList;
import java.util.List;

import wooteco.subway.domain.property.Color;
import wooteco.subway.domain.property.Name;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.SectionSeries;
import wooteco.subway.util.Id;

public class Line {

    @Id
    private final Long id;
    private final Name name;
    private final Color color;
    private final SectionSeries sectionSeries;

    public Line(Long id, Name name, Color color, SectionSeries sectionSeries) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sectionSeries = sectionSeries;
    }

    public Line(Long id, String name, String color, List<Section> sections) {
        this(id, new Name(name), new Color(color), new SectionSeries(sections));
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, new ArrayList<>());
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public void addSection(Section section) {
        sectionSeries.add(section);
    }

    public boolean hasSameNameWith(Line otherLine) {
        return this.name.equals(otherLine.name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    public String getColor() {
        return color.getValue();
    }

    public SectionSeries getSectionSeries() {
        return sectionSeries;
    }

    @Override
    public String toString() {
        return "Line{" +
            "id=" + id +
            ", name=" + name +
            ", color=" + color +
            ", sectionSeries=" + sectionSeries +
            '}';
    }
}
