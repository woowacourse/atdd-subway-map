package wooteco.subway.line.domain;

import wooteco.subway.section.domain.OrderedSections;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;

import java.util.List;
import java.util.Objects;

public class Line {
    private Long id;
    private LineName name;
    private LineColor color;
    private Sections sections;

    public Line(String name, String color, Sections sections) {
        this(null, new LineName(name), new LineColor(color), sections);
    }

    public Line(Long id, String name, String color, Sections sections) {
        this(id, new LineName(name), new LineColor(color), sections);
    }

    public Line(String name, String color, List<Section> sections) {
        this(null, new LineName(name), new LineColor(color), new OrderedSections(sections));
    }

    public Line(Line idLine, OrderedSections sections) {
        this(idLine.getId(), idLine.getName(), idLine.getColor(), sections);
    }

    public Line(Long id, LineName name, LineColor color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public static Line createEntity(Line line, OrderedSections orderedSections) {
        return new Line(line, orderedSections);
    }

    public Long getId() {
        return id;
    }

    public LineName getName() {
        return name;
    }

    public LineColor getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name=" + name +
                ", color=" + color +
                '}';
    }
}

