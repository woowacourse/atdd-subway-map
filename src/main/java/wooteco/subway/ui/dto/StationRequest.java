package wooteco.subway.ui.dto;

import org.hibernate.validator.constraints.Length;

public class StationRequest {

    @Length(min = 1, max = 255)
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
