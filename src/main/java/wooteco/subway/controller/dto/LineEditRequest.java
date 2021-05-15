package wooteco.subway.controller.dto;

import wooteco.subway.domain.Line;

import javax.validation.constraints.NotBlank;

public class LineEditRequest {

    @NotBlank
    private final String name;

    @NotBlank
    private final String color;

    public LineEditRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line toEntity() {
        return new Line(null, name, color, null, null);
    }
}
