package wooteco.subway.line.domain;

public class Line {
    private final Long id;
    private final String name;
    private final String color;

    public Line(Long lineId) {
        this(lineId, null, null);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    public boolean sameAs(Long id) {
        return this.id.equals(id);
    }
}
