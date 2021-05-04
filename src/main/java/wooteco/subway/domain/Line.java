package wooteco.subway.domain;

public class Line {
    private Long id;
    private final String name;
    private final String color;

    public Line(Long id, String name, String color) {
        this(name, color);
        this.id = id;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, Line line) {
        this(id, line.getName(), line.getColor());
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
