package wooteco.subway.domain;

import java.util.List;
import java.util.Objects;

public class Line {
    private final Long id;
    private String name;
    private String color;
    private final Sections sections = new Sections();

    public Line(final Long id, final String name, final String color) {
        validateNotBlank(name);
        validateNotBlank(color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(final String name, final String color) {
        this(null, name, color);
    }

    public void update(final String name, final String color) {
        validateNotBlank(name);
        validateNotBlank(color);
        this.name = name;
        this.color = color;
    }

    public void addAllSections(final List<Section> sections) {
        for (Section section : sections) {
            this.sections.add(section);
        }
    }

    public void addSection(final Section section) {
        sections.add(section);
    }
    public void removeStation(final Station station) {
        sections.removeStation(station);
    }

    public List<Section> getSections() {
        return List.copyOf(sections.values());
    }

    public List<Station> getStations() {
        return List.copyOf(sections.stations());
    }

    private void validateNotBlank(final String string) {
        if (string.isBlank()) {
            throw new IllegalArgumentException("빈 값이 들어올 수 없습니다.");
        }
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
