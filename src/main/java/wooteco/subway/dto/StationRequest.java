package wooteco.subway.dto;

import javax.validation.constraints.NotEmpty;

public class StationRequest {
    @NotEmpty(message = "이름을 입력해주세요!")
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
