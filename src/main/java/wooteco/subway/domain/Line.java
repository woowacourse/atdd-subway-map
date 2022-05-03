package wooteco.subway.domain;

import java.util.Objects;

public class Line {
    private Long id;
    private String name;
    private String  color;

    public Line() {}

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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
        return name.equals(line.name) && color.equals(line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
