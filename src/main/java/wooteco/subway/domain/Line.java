package wooteco.subway.domain;

public class Line {
    private Long id;
    private String name;
    private String color;

    public Line() {
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public boolean isDuplicated(Line line) {
        return this.name.equals(line.name) || this.color.equals(line.color);
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
