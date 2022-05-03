package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private Long id;
    private String name;
    private String color;

    public Line(String name, String color) {
        this.id = null;
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public boolean isSameName(Line line) {
        return this.name.equals(line.name);
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

    public void update(Line line) {
        this.name = line.name;
        this.color = line.color;
    }
}
