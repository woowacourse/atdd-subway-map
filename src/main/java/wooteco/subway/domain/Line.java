package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private static final int NAME_LENGTH = 255;
    private static final int COLOR_LENGTH = 20;

    private final Long id;
    private final String name;
    private final String color;

    public Line() {
        this(null, null, null);
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        validateName(name);
        validateColor(color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    private void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("이름은 비어있을 수 없습니다.");
        }
        if (name.length() > NAME_LENGTH) {
            throw new IllegalArgumentException("이름은 " + NAME_LENGTH + "자를 초과할 수 없습니다.");
        }
    }

    private void validateColor(String color) {
        if (color == null || color.isEmpty()) {
            throw new IllegalArgumentException("색은 비어있을 수 없습니다.");
        }
        if (color.length() > COLOR_LENGTH) {
            throw new IllegalArgumentException("색은 " + COLOR_LENGTH + "자를 초과할 수 없습니다.");
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
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
