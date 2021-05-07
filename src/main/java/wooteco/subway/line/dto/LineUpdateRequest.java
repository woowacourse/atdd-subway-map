package wooteco.subway.line.dto;

import javax.validation.constraints.NotBlank;
import wooteco.subway.line.domain.Line;

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

    public Line toLine(Long id) {
        return Line.of(id, name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
