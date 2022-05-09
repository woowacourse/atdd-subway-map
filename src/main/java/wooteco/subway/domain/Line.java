package wooteco.subway.domain;

public class Line {
    private Long id;
    private String name;
    private String color;

    public Line() {
    }

    private Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static Line of(Long id, String name, String color) {
        return new Line(id, name, color);
    }

    public static Line of(String name, String color) {
        return of(null, name, color);
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

    public boolean isSameName(Line line) {
        return name.equals(line.name);
    }

    public boolean isSameColor(Line line) {
        return color.equals(line.color);
    }
}
