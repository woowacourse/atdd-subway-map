package wooteco.subway.station.dto;

import wooteco.subway.station.Station;

public class StationResponse {

    private Long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(Station newStation) {
        this.id = newStation.getId();
        this.name = newStation.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
