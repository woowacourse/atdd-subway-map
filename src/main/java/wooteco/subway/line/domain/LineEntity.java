package wooteco.subway.line.domain;

public class LineEntity {
    private final Long id;
    private String name;
    private String color;

    public LineEntity(final String name, final String color) {
        this(0L, name, color);
    }

    public LineEntity(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String color() {
        return color;
    }

    public boolean sameName(final String name) {
        return this.name.equals(name);
    }

    public boolean sameId(final Long id) {
        return this.id.equals(id);
    }

    public void changeName(final String name) {
        this.name = name;
    }

    public void changeColor(String color) {
        this.color = color;
    }
}
