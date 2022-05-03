package wooteco.subway.domain;

public class Line {
    private Long id;
    private String name;
    private String color;

    public Line() {
    }

    public Line(Long id, String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
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
