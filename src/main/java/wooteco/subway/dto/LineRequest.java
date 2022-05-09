package wooteco.subway.dto;

import javax.validation.constraints.Size;

public class LineRequest {
    @Size(min = 1, max = 10, message = "노선 이름의 길이는 1 이상 10 이하여야 합니다.")
    private String name;
    @Size(min = 1, max = 20, message = "노선 색의 길이는 1 이상 20 이하여야 합니다.")
    private String color;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
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
