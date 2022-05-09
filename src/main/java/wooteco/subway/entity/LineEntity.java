package wooteco.subway.entity;

public class LineEntity {

    private final String name;
    private final String color;

    private LineEntity(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static LineEntity of(String name, String color) {
        return new LineEntity(name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
