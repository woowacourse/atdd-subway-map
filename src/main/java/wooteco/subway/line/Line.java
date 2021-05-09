package wooteco.subway.line;

import wooteco.subway.section.Sections;
import wooteco.subway.station.Station;

import java.util.List;

public class Line {

    private Long id;
    private String name;
    private String color;
    private Sections sections;

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
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

    public List<Station> path() {
        return sections.path();
    }
}
