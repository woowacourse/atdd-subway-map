package wooteco.subway.dto.line;

import javax.validation.constraints.NotBlank;
import wooteco.subway.domain.Line;

public class LineUpdateRequest {

    @NotBlank(message = "line 이름은 공백 혹은 null이 들어올 수 없습니다.")
    private String name;

    @NotBlank(message = "line 색상은 공백 혹은 null이 들어올 수 없습니다.")
    private String color;

    private LineUpdateRequest() {
    }

    public LineUpdateRequest(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public Line toLineWithId(final Long lineId) {
        return new Line(lineId, name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
