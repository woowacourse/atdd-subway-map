package wooteco.subway.dto;

import lombok.Getter;

@Getter
public class StationRequest {
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }
}
