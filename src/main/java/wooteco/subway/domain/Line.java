package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private final String name;
    private final String color;
    private final Long id;

    public Line(Long id, String name, String color) {
        this.id = id;
        if (Objects.isNull(name) || Objects.isNull(color)) {
            throw new IllegalArgumentException("[ERROR] 잘못된 입력값입니다.");
        }
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this(null, name, color);
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
        return Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public boolean isSameName(String name) {
        return name.equals(this.name);
    }
}
