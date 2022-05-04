package wooteco.subway.domain;

public class Line {

    private Long id;
    private final String name;
    private final String color;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return this.id;
    }
}
