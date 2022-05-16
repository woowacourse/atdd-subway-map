package wooteco.subway.domain;

import wooteco.subway.exception.DataLengthException;

import java.util.Objects;

public class Line {

    private static final int MAX_NAME_LENGTH = 20;
    private static final int MAX_COLOR_LENGTH = 20;

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
        if (name.isEmpty() || name.length() > MAX_NAME_LENGTH) {
            throw new DataLengthException("노선 이름이 빈 값이거나 최대 범위(" + MAX_NAME_LENGTH + ")를 초과했습니다.");
        }
        if (color.isEmpty() || color.length() > MAX_COLOR_LENGTH) {
            throw new DataLengthException("노선 색이 빈 값이거나 최대 범위(" + MAX_COLOR_LENGTH + "를 초과했습니다.");
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
        return Objects.equals(getId(), line.getId()) &&
                Objects.equals(getName(), line.getName()) &&
                Objects.equals(getColor(), line.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getColor());
    }
}
