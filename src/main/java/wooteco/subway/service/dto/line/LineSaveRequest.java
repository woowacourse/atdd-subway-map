package wooteco.subway.service.dto.line;


public class LineSaveRequest {

    private final String name;
    private final String color;

    public LineSaveRequest(String name, String color) {
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
