package wooteco.subway.line;

public class LinesResponse {
    private Long id;
    private String name;
    private String color;

    public LinesResponse(final Long id, final String name, final String color) {
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
