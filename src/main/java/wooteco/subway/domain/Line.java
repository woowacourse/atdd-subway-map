package wooteco.subway.domain;

import java.util.List;
import java.util.Objects;

public class Line {
    private Long id;
    private final String name;
    private final String color;
    private Sections sections;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.sections = new Sections(upStation, downStation, distance);
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        this.sections.add(upStation, downStation, distance);
    }

    public void deleteStation(Station station) {
        this.sections.delete(station);
    }

    public void setSectionsFrom(List<Section> sections) {
        this.sections = new Sections(sections);
    }

    public Sections getSections() {
        return this.sections;
    }

    public List<Station> getStations() {
        return this.sections.getStations();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

    public boolean isRemovable() {
        return this.sections.isRemovable();
    }
}
