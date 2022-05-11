package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private final Long id;
    private final String name;
    private final String color;

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
        validateField();
    }

    public Line(String name, String color) {
        this(0L, name, color);
    }

    private void validateField() {
        if (name.isBlank()) {
            throw new IllegalArgumentException("노선의 이름은 공백, 빈값으로 할 수 없습니다.");
        }
        if (color.isBlank()) {
            throw new IllegalArgumentException("노선의 색깔은 공백, 빈값으로 할 수 없습니다.");
        }
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
