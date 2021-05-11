package wooteco.subway.presentation.station.dto;

import wooteco.subway.domain.station.Station;

import java.beans.ConstructorProperties;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

    @ConstructorProperties({"id", "name"})
    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
