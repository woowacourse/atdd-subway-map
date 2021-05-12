package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;

public class LineUpdateRequest {
    @NotBlank(message = "노선의 이름은 필수로 입력하여야 합니다.")
    private String name;
    @NotBlank(message = "노선의 색상은 필수로 입력하여야 합니다.")
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
