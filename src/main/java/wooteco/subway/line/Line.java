package wooteco.subway.line;

public class Line {

    private Long id;
    private String name;
    private String color;

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
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

    public boolean hasSameName(Line lineToSave) {
        return name.equals(lineToSave.getName());
    }

    public boolean hasSameColor(Line lineToSave) {
        return color.equals(lineToSave.getColor());
    }
}
