package wooteco.subway.domain.line;

import java.util.Objects;
import wooteco.subway.entity.LineEntity;

public class Line {

    private static final String BLANK_OR_NULL_EXCEPTION = "노선 정보가 입력되지 않았습니다.";

    private final Long id;
    private final String name;
    private final String color;

    public Line(Long id, String name, String color) {
        validateNotBlank(name);
        validateNotBlank(color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    private void validateNotBlank(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(BLANK_OR_NULL_EXCEPTION);
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

    public LineEntity toEntity() {
        return new LineEntity(id, name, color);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line2 = (Line) o;
        return Objects.equals(id, line2.id)
                && Objects.equals(name, line2.name)
                && Objects.equals(color, line2.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
