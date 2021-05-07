package wooteco.subway.station.dto;

import wooteco.subway.station.Station;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(Station newStation) {
        this(newStation.getId(), newStation.getName());
    }

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
