package wooteco.subway.domain;

import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Sections sections;
    // private List<Section> sections;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this(name, color);
        this.id = id;
    }

    public Line(Long id, String name, String color, Section section) {
        this(id, name, color);
        this.sections = Sections.of(section);
    }

    public Line(Long id, String name, String color, List<Section> sections) {
        this(id, name, color);
        this.sections = Sections.of(sections);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public List<Station> getStations() {
        return sections.getStations();
        // List<Station> stations = new ArrayList<>();
        // for (Section section : sections) {
        //     stations.add(section.getUpStation());
        //     stations.add(section.getDownStation());
        // }
        // return stations.stream()
        //     .distinct()
        //     .collect(Collectors.toList());
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
