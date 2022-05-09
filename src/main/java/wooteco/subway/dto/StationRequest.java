package wooteco.subway.dto;

import javax.validation.constraints.Size;

public class StationRequest {
    @Size(min = 1, max = 10, message = "역 이름의 길이는 1 이상 10 이하여야 합니다.")
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
