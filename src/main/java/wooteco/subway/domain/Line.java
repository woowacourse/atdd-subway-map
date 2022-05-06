package wooteco.subway.domain;

public class Line {

    private final Long id;
    private final String name;
    private final String color;

    public Line(Long id, String name, String color) {
        validateNotNull(name, "name");
        validateNotNull(color, "color");
        this.id = id;
        this.name = name;
        this.color = color;
    }

    private void validateNotNull(String input, String param) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(String.format("%s은 필수 입력값입니다.", param));
        }
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public boolean hasSameNameWith(Line otherLine) {
        return this.name.equals(otherLine.name);
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
