package wooteco.subway.dto.request;

import wooteco.subway.domain.Station;

public class StationRequest {
    private String name;

    private StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toEntity() {
        return new Station(this.name);
    }
}
