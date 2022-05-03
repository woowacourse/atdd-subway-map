package wooteco.subway.dto.request;

public class LineRequest {

    private String name;
    private String color;

    public LineRequest() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "LineRequest{" + "name='" + name + '\'' + ", color='" + color + '\'' + '}';
    }
}
