package wooteco.subway.line.api.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class LineUpdateRequest {
    @Size(min = 2, message = "노선 이름은 최소 2글자 이상만 가능합니다.")
    private String name;

    @NotEmpty(message = "노선 색을 지정해야합니다.")
    private String color;

    private LineUpdateRequest() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
