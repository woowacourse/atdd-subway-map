package wooteco.subway.line;

public class Line {
    private final String name;
    private final String color;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
