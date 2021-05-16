package wooteco.subway.web.dto;

import javax.validation.constraints.NotBlank;
import wooteco.subway.domain.Line;

public class LineUpdateRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String color;

    public LineUpdateRequest() {
    }

    public LineUpdateRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line toEntity() {
        return new Line(name, color);
    }
}
