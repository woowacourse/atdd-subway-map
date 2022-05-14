package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;

public class LineEditRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String color;

    public LineEditRequest() {
    }

    public LineEditRequest(String name, String color) {
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
