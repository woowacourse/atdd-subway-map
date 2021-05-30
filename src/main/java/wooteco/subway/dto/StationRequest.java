package wooteco.subway.dto;

import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.validator.Name;

public class StationRequest {

    @Name
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
