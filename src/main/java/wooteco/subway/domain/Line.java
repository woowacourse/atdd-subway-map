package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private Long id;
    private String name;
    private String color;

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        validateArgument(name, color);

        this.id = id;
        this.name = name;
        this.color = color;
    }

    private void validateArgument(String name, String color) {
        if (name.isBlank() || color.isBlank()) {
            throw new IllegalArgumentException("노선의 이름 혹은 색이 공백일 수 없습니다.");
        }
        if (name.length() >= 255 || color.length() >= 20) {
            throw new IllegalArgumentException("노선의 이름 혹은 색이 너무 깁니다.");
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
        if (!(o instanceof Line)) {
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
