package wooteco.subway.dto.request;

import javax.validation.constraints.NotBlank;
import wooteco.subway.domain.Line;

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

    public Line toLine(final Long id) {
        return new Line(id, name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
