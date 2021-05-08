package wooteco.subway.line.domain;

public class Line {

    private Long id;
    private String name;
    private String color;

    private Line() {
    }

    private Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color) {
        return new Line(null, name, color);
    }

    public static Line of(Long id, String name, String color) {
        return new Line(id, name, color);
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
