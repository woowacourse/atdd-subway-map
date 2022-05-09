package wooteco.subway.domain.line;

public class Line {

    private static final long TEMPORARY_ID = 0L;

    private final Long id;
    private final LineName name;
    private final LineColor color;

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = new LineName(name);
        this.color = new LineColor(color);
    }

    public Line(String name, String color) {
        this(TEMPORARY_ID, name, color);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
    }

    public String getColor() {
        return color.getColor();
    }
}
