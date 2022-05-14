package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Line {

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

    public List<Section> addSection(Section section) {
        return sectionSeries.add(section);
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

    public List<Section> getSections() {
        return List.copyOf(sectionSeries.getSections());
    }
}
