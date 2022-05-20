package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Line {

    private final Long id;
    private final String name;
    private final String color;
    private final Sections sections;

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, null);
    }

    public Line(String name, String color, Sections sections) {
        this(null, name, color, sections);
    }

    public static Line of(Line line, Sections sections) {
        return new Line(line.getId(), line.getName(), line.getColor(), sections);
    }

    public List<Station> getStations() {
        final List<Section> sections = this.sections.getSections();

        List<Station> stations = getStations(sections);

        return stations.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Station> getStations(List<Section> sections) {
        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations;
    }

    public boolean emptyStations() {
        return Objects.isNull(sections);
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
