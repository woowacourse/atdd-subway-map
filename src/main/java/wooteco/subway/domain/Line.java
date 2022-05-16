package wooteco.subway.domain;

public class Line {

    private Long id;
    private String name;
    private String color;

    public Line(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(final String name, final String color) {
        this(null, name, color);
    }

    public void update(final Line newLine) {
        name = newLine.name;
        color = newLine.color;
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

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
