package wooteco.subway.line.domain;

import wooteco.subway.name.domain.LineName;
import wooteco.subway.name.domain.Name;

public class Line {
    private Long id;
    private Name name;
    private String color;

    public Line(final String name, final String color) {
        this(new LineName(name), color);
    }

    public Line(final Name name, final String color) {
        this(0L, name, color);
    }

    public Line(final Long id, final String name, final String color) {
        this(id, new LineName(name), color);
    }

    public Line(final Long id, final Name name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name.name();
    }

    public String color() {
        return color;
    }

    public boolean sameName(final String name) {
        return this.name.sameName(name);
    }

    public boolean sameId(final Long id) {
        return this.id.equals(id);
    }

    public void changeName(final String name) {
        this.name = this.name.changeName(name);
    }

    public void changeColor(String color) {
        this.color = color;
    }
}
