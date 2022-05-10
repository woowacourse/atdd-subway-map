package wooteco.subway.dto.request;

import javax.validation.constraints.NotBlank;

public class UpdateLineRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String color;

    private UpdateLineRequest() {
    }

    public UpdateLineRequest(final String name, final String color) {
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
