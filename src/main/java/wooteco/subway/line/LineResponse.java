package wooteco.subway.line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;

    public LineResponse(final Line line) {
        this(line.getId(), line.getName(), line.getName());
    }

    public LineResponse(final Long id, final String name, final String color) {
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
}
