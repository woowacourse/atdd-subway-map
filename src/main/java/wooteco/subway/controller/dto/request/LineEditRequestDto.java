package wooteco.subway.controller.dto.request;

public class LineEditRequestDto {

    private String name;
    private String color;

    public LineEditRequestDto() {
    }

    public LineEditRequestDto(String name, String color) {
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
