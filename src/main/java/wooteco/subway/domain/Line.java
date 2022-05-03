package wooteco.subway.domain;

public class Line {

    private Long id;
    private String name;
    private String color;

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public boolean isSameName(String lineName) {
        return name.equals(lineName);
    }

    public boolean isSameId(Long lineId) {
        return id == lineId;
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
