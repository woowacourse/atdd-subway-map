package wooteco.subway.line.domain;

public class Line {
    private Long id;
    private LineName name;
    private String color;

    public Line(String name, String color) {
        this(null, new LineName(name), color);
    }

    public Line(Long id, LineName name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this(id, new LineName(name), color);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    public String getColor() {
        return color;
    }
}
