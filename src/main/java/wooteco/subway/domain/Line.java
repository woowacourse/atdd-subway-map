package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private Long id;
    private String name;
    private String color;
    private Sections sections;

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, null);
    }

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "값을 입력해주세요" + "name");
        this.color = Objects.requireNonNull(color, "값을 입력해주세요" + "color");
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

    public Sections getSections() {
        return sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Line)) {
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
