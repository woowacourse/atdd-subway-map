package wooteco.subway.dto;

public class LineRequest {

    private String name;
    private String color;

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}
