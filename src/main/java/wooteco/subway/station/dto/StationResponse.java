package wooteco.subway.station.dto;

import wooteco.subway.station.domain.Station;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(final Station station) {
        this(station.id(), station.name());
    }

    public StationResponse(final Long id, final String name) {
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
