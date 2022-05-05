package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private final Long id;
    private final String name;
    private final String color;

    public Line(Long id, String name, String color) {
        validateName(name);
        validateColor(color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    private void validateName(String name) {
        Objects.requireNonNull(name, "이름은 Null 일 수 없습니다.");
        validateNameLength(name);
    }

    private void validateColor(String color) {
        Objects.requireNonNull(color, "색상은 Null 일 수 없습니다.");
        validateColorLength(color);
    }

    private void validateNameLength(String name) {
        validateLength(name.length(), 30, "이름은 1~30 자 이내여야 합니다.");
    }

    private void validateColorLength(String color) {
        validateLength(color.length(), 20, "색상은 1~20 자 이내여야 합니다.");
    }

    private void validateLength(int length, int max, String message) {
        if (length < 1 || length > max) {
            throw new IllegalArgumentException(message);
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
}
