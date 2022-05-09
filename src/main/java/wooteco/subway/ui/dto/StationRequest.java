package wooteco.subway.ui.dto;

import javax.validation.constraints.Size;

public class StationRequest {
    @Size(min = 1, max = 25)
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
