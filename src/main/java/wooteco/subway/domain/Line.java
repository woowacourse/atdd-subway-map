package wooteco.subway.domain;

public class Line {
    private final Long id;
    private final String name;
    private final String color;

    public Line(Long id, String name, String color) {
        validateNameLength(name);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    private void validateNameLength(String name) {
        if (name.isBlank() || name.length() > 20) {
            throw new IllegalArgumentException("노선 이름은 최소 1글자이상 20글자 이하여야 합니다.");
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
