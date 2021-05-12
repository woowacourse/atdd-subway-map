package wooteco.subway.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class StationRequest {

    @NotBlank(message = "이름은 입력해주세요.")
    @Pattern(regexp = "^[가-힣|0-9]*역$")
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
