package wooteco.subway.domain;

import java.util.List;
import java.util.Optional;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Sections sections;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this(name, color);
        this.id = id;
    }

    private Line(Long id, String name, String color, List<Section> sections) {
        this(id, name, color);
        this.sections = Sections.of(sections);
    }

    public static Line from(Line line, List<Section> sections) {
        return new Line(line.getId(), line.getName(), line.getColor(), sections);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Optional<Section> insertSection(Section section) {
        return sections.insert(section);
    }

    public UpdatedSection deleteStation(Station station) {
        return sections.delete(station);
    }

    public List<Station> getStations() {
        return sections.getStations();
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Line line = (Line)o;

        return getName() != null ? getName().equals(line.getName()) : line.getName() == null;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }
}
