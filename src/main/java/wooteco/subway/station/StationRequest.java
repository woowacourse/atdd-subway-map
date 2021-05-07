package wooteco.subway.station;

import com.fasterxml.jackson.annotation.JsonCreator;

public class StationRequest {
    private final String name;

    @JsonCreator
    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
