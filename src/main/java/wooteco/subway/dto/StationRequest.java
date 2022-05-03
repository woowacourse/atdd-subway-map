package wooteco.subway.dto;

import wooteco.subway.domain.Station;

public class StationRequest {

    private String name;

    private StationRequest() {
    }

    public Station toStation() {
        return new Station(name);
    }

    public String getName() {
        return name;
    }
}
