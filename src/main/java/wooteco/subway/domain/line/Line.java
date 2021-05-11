package wooteco.subway.domain.line;

import wooteco.subway.domain.section.Sections;

public class Line {

    private Long id;
    private String color;
    private String name;
    private Sections sections;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    public Line(Long id, String color, String name, Sections sections) {
        this.id = id;
        this.color = color;
        this.name = name;
        this.sections = sections;
    }

    public Long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public Sections getSections() {
        return sections;
    }
}
