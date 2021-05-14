package wooteco.subway.line.domain;

import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.domain.Stations;

import java.util.Objects;

public class Line {
    private final Long id;
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

    public Long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public Stations getAllStations() {
        return sections.getOrderedStations();
    }

    public void setSections(final Sections sections) {
        this.sections = sections;
    }

    public boolean hasSameName(final Line that) {
        return this.name.equals(that.name);
    }

    public boolean hasId(final Long id) {
        return this.id.equals(id);
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
