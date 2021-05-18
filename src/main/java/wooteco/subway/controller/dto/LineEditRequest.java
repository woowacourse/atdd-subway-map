package wooteco.subway.controller.dto;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import wooteco.subway.domain.Line;

public class LineEditRequest {

    @NotBlank
    private final String name;

    @NotBlank
    private final String color;

    @JsonCreator
    public LineEditRequest(
            @JsonProperty(value = "name") String name,
            @JsonProperty(value = "color") String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line toEntity(Line line) {
        return new Line(line.getId(), name, color, line.getSections());
    }
}
