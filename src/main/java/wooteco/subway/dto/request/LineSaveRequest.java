package wooteco.subway.dto.request;

public class LineSaveRequest {

    private final String name;
    private final String color;

    private LineSaveRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static LineSaveRequest of(String name, String color) {
        return new LineSaveRequest(name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
