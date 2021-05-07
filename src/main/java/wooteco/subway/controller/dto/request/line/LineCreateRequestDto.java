package wooteco.subway.controller.dto.request.line;

public class LineCreateRequestDto {
    private String name;
    private String color;

    public LineCreateRequestDto() {
    }

    public LineCreateRequestDto(String name, String color) {
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
