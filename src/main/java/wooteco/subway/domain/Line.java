package wooteco.subway.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Line {
    private Long id;
    private Name name;
    private String color;
    private Sections sections;

    private Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = new Name(name);
        this.color = color;
        this.sections = sections;
    }

    public static Line initialCreateWithoutId(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(null, name, color, new Sections(upStation, downStation, distance));
    }

    public static Line createWithoutSection(Long id, String name, String color) {
        return new Line(id, name, color,null);
    }

    public static Line createWithId(Long id, String name, String color, List<Section> sections) {
        return new Line(id, name, color, new Sections(sections));
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public void deleteSection(Station station) {
        sections.deleteSection(station);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections.getSections());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
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
