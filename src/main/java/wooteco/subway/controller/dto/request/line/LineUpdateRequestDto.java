package wooteco.subway.controller.dto.request.line;

import javax.validation.constraints.NotBlank;

public class LineUpdateRequestDto {
    @NotBlank
    private String name;
    @NotBlank
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
