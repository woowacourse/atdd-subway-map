package wooteco.subway.web.dto;

import javax.validation.constraints.NotEmpty;
import wooteco.subway.domain.station.Station;

public class StationRequest {

    @NotEmpty
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public Station toEntity() {
        return new Station(name);
    }

    public String getName() {
        return name;
    }
}
