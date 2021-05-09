package wooteco.subway.dto.line.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class LineUpdateRequest {
    @NotBlank
    @Pattern(regexp = "^[가-힣|A-Z|a-z| 0-9]*선$")
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
}
