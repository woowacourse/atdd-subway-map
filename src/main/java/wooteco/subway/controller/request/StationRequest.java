package wooteco.subway.controller.request;

import wooteco.subway.domain.Station;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class StationRequest {
    @NotNull
    @Pattern(regexp = "^[가-힣|0-9]*역$")
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toEntity() {
        return new Station(name);
    }
}
