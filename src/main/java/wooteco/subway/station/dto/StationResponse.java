package wooteco.subway.station.dto;

import wooteco.subway.station.Station;

public class StationResponse {
    private long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(Station station) {
        this(station.getId(), station.getName());
    }

    public StationResponse(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
