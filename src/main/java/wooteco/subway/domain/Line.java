package wooteco.subway.domain;

import wooteco.subway.exception.DataLengthException;

import java.util.Objects;

public class Line {

    private final Long id;
    private final String name;
    private final String color;

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        validateDataSize(name, color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    private void validateDataSize(String name, String color) {
        if (name.isEmpty() || name.length() > 255) {
            throw new DataLengthException("노선 이름이 빈 값이거나 최대 범위를 초과했습니다.");
        }
        if (color.isEmpty() || color.length() > 20) {
            throw new DataLengthException("노선 색이 빈 값이거나 최대 범위를 초과했습니다.");
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(getId(), line.getId()) && Objects.equals(getName(), line.getName()) && Objects.equals(getColor(), line.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getColor());
    }
}
