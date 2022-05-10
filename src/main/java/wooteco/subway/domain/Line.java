package wooteco.subway.domain;

public class Line {

    private final Long id;
    private final Name name;
    private final Color color;

    public Line(Long id, Name name, Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this(id, new Name(name), new Color(color));
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public boolean hasSameNameWith(Line otherLine) {
        return this.name.equals(otherLine.name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    public String getColor() {
        return color.getValue();
    }
}
