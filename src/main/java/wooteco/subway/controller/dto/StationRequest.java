package wooteco.subway.controller.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class StationRequest {

    @NotNull(message = "이름을 입력해주세요.")
    @NotEmpty(message = "이름은 공백을 허용하지 않습니다.")
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
