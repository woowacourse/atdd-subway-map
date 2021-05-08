package wooteco.subway.dto;

public class LineSaveRequest {
    private String name;
    private String color;

    public LineSaveRequest() {
    }

    public LineSaveRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
