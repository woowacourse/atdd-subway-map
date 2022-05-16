package wooteco.subway.service.dto;

import javax.validation.constraints.NotBlank;

public class LineUpdateRequest {

    @NotBlank(message = "이름 값을 입력해주세요.")
    private String name;

    @NotBlank(message = "색상 값을 입력해주세요.")
    private String color;

    private LineUpdateRequest() {
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
