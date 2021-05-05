package wooteco.subway.line.ui.dto;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;

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
