package wooteco.subway.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Line {
    private Long id;
    private String name;
    private String color;
//    private List<Section> sections;
    private Sections sections;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.sections = new Sections(upStation, downStation, distance);
//        sections = new ArrayList<>();
//        sections.add(new Section(upStation, downStation, distance));
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, List<Section> sections) {
        this.name = name;
        this.color = color;
        this.sections = new Sections(sections);
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public List<Station> getStations() {
//        List<Station> stations = new ArrayList<>();
//        for (Section section : sections) {
//            stations.add(section.getUpStation());
//            stations.add(section.getDownStation());
//        }
//        return stations.stream()
//                .distinct()
//                .collect(Collectors.toList());
        return sections.getStations();
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections.getSections());
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects
                .equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
