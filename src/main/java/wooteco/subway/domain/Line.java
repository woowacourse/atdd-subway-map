package wooteco.subway.domain;

public class Line {

    private Id id;
    private Name name;
    private Color color;

    public Line() {
    }

    public Line(final String name, final String color) {
        this(null, new Name(name), new Color(color));
    }

    public Line(final Long id, final String name, final String color) {
        this(new Id(id), new Name(name), new Color(color));
    }

    public Line(final Id id, final Name name, final Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id.getValue();
    }

    public String getName() {
        return name.getValue();
    }

    public String getColor() {
        return color.getValue();
    }
}
