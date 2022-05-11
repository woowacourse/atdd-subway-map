package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class StationRequest {
    @NotBlank(message = "역 이름은 공백일 수 없습니다.")
    @NotNull(message = "역 이름은 공백일 수 없습니다.")
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
