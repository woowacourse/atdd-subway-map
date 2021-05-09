package wooteco.subway.station.controller.dto;

import wooteco.subway.station.domain.Station;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public StationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName().text();
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}