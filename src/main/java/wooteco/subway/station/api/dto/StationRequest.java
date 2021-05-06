package wooteco.subway.station.api.dto;

import javax.validation.constraints.Size;

public class StationRequest {

    @Size(min = 2, message = "역 이름은 최소 2글자 이상이어야 합니다.")
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
