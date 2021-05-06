package wooteco.subway.domain.line;

public class Line {

    private Long id;
    private String color;
    private String name;

    public Line() {
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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
