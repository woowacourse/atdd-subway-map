package wooteco.subway.dto;

public class LineCreateRequest {

    private String name;
    private String color;

    public LineCreateRequest() {
    }

    public LineCreateRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
