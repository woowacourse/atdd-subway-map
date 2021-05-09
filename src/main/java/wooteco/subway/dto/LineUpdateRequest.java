package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;

public class LineUpdateRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String color;

    private LineUpdateRequest() {
    }

    private LineUpdateRequest(String name, String color) {
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
