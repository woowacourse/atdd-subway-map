package wooteco.subway.dto;

import wooteco.subway.utils.ExceptionMessage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;


public class SimpleLineRequest {

    @NotBlank(message = ExceptionMessage.NO_NAME_BLANK)
    private String name;
    @NotEmpty(message = ExceptionMessage.NO_COLOR_BLANK)
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
