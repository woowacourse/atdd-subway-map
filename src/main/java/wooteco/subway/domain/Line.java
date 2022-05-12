package wooteco.subway.domain;

import java.util.ArrayList;

public class Line {

    private final Long id;
    private final String name;
    private final String color;
    private final Sections sections;
    private final Long upStationId;

    public Line(final Long id, final String name, final String color, final Sections sections, final Long upStationId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
        this.upStationId = upStationId;
    }

    public Line(final Long id, final String name, final String color, final Long upStationId) {
        this(id, name, color, new Sections(new ArrayList<>()), upStationId);
    }

    public Line(final String name, final String color) {
        this(null, name, color, new Sections(new ArrayList<>()), 0L);
    }

    public void addSection(final Section newSection) {
        sections.addIfPossible(newSection);
    }

    public boolean isSameId(final Long id) {
        return this.id.equals(id);
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

    public Long getUpStationId() {
        return upStationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Line)) {
            return false;
        }

        Line line = (Line) o;
        return id.equals(line.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Line{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
