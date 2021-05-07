package wooteco.subway.domain.line;

public class Line {
    private Long id;
    private String color;
    private LineName name;

    public Line(String color, String name) {
        this.color = color;
        this.name = new LineName(name);
    }

    public Line(Long id, String color, String name) {
        this(id, color, new LineName(name));
    }

    public Line(Long id, String color, LineName name) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name.getName();
    }
}
