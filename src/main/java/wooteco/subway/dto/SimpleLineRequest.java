package wooteco.subway.dto;

import org.springframework.lang.NonNull;

import javax.validation.constraints.NotEmpty;

public class SimpleLineRequest {

    //TODO: ControllerTest에서 검증해보기!
    @NotEmpty
    private String name;
    @NotEmpty
    private String color;

    public SimpleLineRequest() {
    }

    public SimpleLineRequest(String name, String color) {
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
