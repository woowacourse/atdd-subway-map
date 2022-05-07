package wooteco.subway.dto;

import wooteco.subway.domain.Station;

public class StationResponse {

    private final Long id;
    private final String name;

    public StationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
