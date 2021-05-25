package wooteco.subway.line.domain;

import wooteco.subway.line.domain.section.Section;
import wooteco.subway.line.domain.section.Sections;

import java.util.List;
import java.util.Objects;

public class Line {
    private final Long id;
    private final String name;
    private final String color;
    private final Sections sections;

    public Line(String name, String color) {
        this(null, name, color, Sections.empty());
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, Sections.empty());
    }

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public boolean isSameName(final String name) {
        return this.name.equals(name);
    }

    public boolean isSameId(final Long id) {
        return this.id.equals(id);
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public Line update(final String name, final String color) {
        return new Line(this.id, name, color, this.sections);
    }

    public List<Long> sortingSectionIds() {
        return sections.sortSectionsId();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
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
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, sections);
    }
}
