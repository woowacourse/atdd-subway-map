package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private List<Section> sections;

    public Line(String name, String color) {
        this(0L, name, color, new ArrayList<>());
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, new ArrayList<>());
    }

    public Line(Long id, String name, String color, List<Section> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String color() {
        return color;
    }

    public List<Section> sections() {
        return sections;
    }

    public boolean sameId(final Long id) {
        return this.id.equals(id);
    }

    public boolean sameName(final String name) {
        return this.name.equals(name);
    }

    public void changeName(final String name) {
        this.name = name;
    }

    public void changeColor(String color) {
        this.color = color;
    }
}
