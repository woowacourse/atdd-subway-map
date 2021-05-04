package wooteco.subway.controller.dto.request;

public class LineUpdateRequestDto {
    private String name;
    private String color;

    public LineUpdateRequestDto() {
    }

    public LineUpdateRequestDto(String name, String color) {
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
