package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;

public class StationRequest {

    @NotBlank(message = "이름이 공백일 수 없습니다")
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
