package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    private Long id;
    private Name name;
    private String color;


    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        Objects.requireNonNull(name, ERROR_NULL);
        Objects.requireNonNull(color, ERROR_NULL);
        this.id = id;
        this.name = new Name(name);
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
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
        return name.equals(line.name) && color.equals(line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }

    @Override
    public String toString() {
        return "Line{" + "id=" + id + ", name='" + name + '\'' + ", color='" + color + '\'' + '}';
    }
}
