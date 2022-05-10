package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private Long id;
    private String name;
    private String color;
    private Sections sections;

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        if (Objects.isNull(name) || Objects.isNull(color)) {
            throw new IllegalArgumentException("[ERROR] 잘못된 입력값입니다.");
        }
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, null);
    }

    public Line(String name, String color) {
        this(null, name, color, null);
    }

    public boolean isSameName(String name) {
        return name.equals(this.name);
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

    public Sections getSections() {
        return sections;
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
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sections=" + sections +
                '}';
    }
}
