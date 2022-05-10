package wooteco.subway.dto.request;

public class UpdateLineRequest {

    private String name;
    private String color;

    public UpdateLineRequest() {
    }

    public UpdateLineRequest(String name, String color) {
        this.name = name;
        this.color = color;
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
        return "UpdateLineRequest{" + "name='" + name + '\'' + ", color='" + color + '\'' + '}';
    }
}
