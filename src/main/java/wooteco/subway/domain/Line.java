package wooteco.subway.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Line {
    private Long id;
    private String name;
    private String color;
    private final Set<Station> stations = new HashSet<>();
    private final Set<Section> sections = new HashSet<>();

    public Line(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public Line(final long id, final String name, final String color, final Section section) {
        this.id = id;
        this.name = name;
        this.color = color;
        sections.add(section);
        stations.addAll(Set.of(section.getUpStation(), section.getDownStation()));
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

    public void update(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public boolean containsBoth(final Station first, final Station second) {
        return stations.containsAll(Set.of(first, second));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Line line = (Line) o;
        return id.equals(line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
