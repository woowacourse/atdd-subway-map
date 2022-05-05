package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;
import wooteco.subway.domain.Line;

public class LineRequest {

    @NotBlank(message = "line 이름은 공백 혹은 null이 들어올 수 없습니다.")
    private String name;

    @NotBlank(message = "line 색상은 공백 혹은 null이 들어올 수 없습니다.")
    private String color;

    private LineRequest() {
    }

    public Line toLine() {
        return new Line(name, color);
    }

    public Line toLineWithId(final Long id) {
        return new Line(id, name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
