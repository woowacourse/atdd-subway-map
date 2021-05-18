package wooteco.subway.line.domain;

import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.domain.Stations;

import java.util.Objects;

public class Line {
    private Long id;
    private final String color;
    private final String name;
    private Sections sections;

    public Line(final String color, final String name) {
        this(null, color, name, new Sections());
    }

    public Line(final Long id, final String color, final String name) {
        this(id, color, name, new Sections());
    }

    public Line(final Long id, final String color, final String name, final Sections sections) {
        this.id = id;
        this.color = color;
        this.name = name;
        this.sections = sections;
    }

    public Stations getAllStations() {
        return sections.getOrderedStations();
    }

    public boolean hasName(final String name) {
        return this.name.equals(name);
    }

    public boolean hasId(final Long id) {
        return this.id.equals(id);
    }

    public Long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public void setSections(final Sections sections) {
        this.sections = sections;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
