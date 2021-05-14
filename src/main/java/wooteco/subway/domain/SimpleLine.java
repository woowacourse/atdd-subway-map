package wooteco.subway.domain;

public class SimpleLine {
    private String color;
    private String name;

    public SimpleLine(String color, String name) {
        this.color = color;
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public Line toLine() {
        return new Line(color, name);
    }
}
