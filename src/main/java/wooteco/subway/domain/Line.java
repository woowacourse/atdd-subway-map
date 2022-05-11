package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private final Long id;
    private final String name;
    private final String color;
    private final Sections sections;

    public Line(String name, String color) {
        this(null, name, color, new Sections(new ArrayList<>()));
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, new Sections(new ArrayList<>()));
    }

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public List<Station> getSortedStations() {
        return sections.getSortedStations();
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
}
