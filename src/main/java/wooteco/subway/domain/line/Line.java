package wooteco.subway.domain.line;

public class Line {
    private Long id;
    private String color;
    private String name;

    public Line(String color, String name) {
        this.color = color;
        this.name = name;
    }

    public Line(Long id, String color, String name) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}
