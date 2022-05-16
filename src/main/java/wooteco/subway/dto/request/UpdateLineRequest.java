package wooteco.subway.dto.request;

import javax.validation.constraints.NotBlank;

public class UpdateLineRequest {

    @NotBlank(message = "노선의 이름이 입력되지 않았습니다.")
    private String name;

    @NotBlank(message = "노선의 색상이 입력되지 않았습니다.")
    private String color;

    public UpdateLineRequest() {
    }

    public UpdateLineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "UpdateLineRequest{" + "name='" + name + '\'' + ", color='" + color + '\'' + '}';
    }
}
