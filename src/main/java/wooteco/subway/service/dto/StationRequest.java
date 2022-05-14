package wooteco.subway.service.dto;

import javax.validation.constraints.NotBlank;

public class StationRequest {

    @NotBlank(message = "이름 값을 입력해주세요.")
    private String name;

    private StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
