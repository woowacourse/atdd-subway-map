package wooteco.subway.dto;

import javax.validation.constraints.NotEmpty;

public class LineUpdateRequest {

    @NotEmpty
    private String name;
    @NotEmpty
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
}
