package wooteco.subway.dto;

import wooteco.subway.domain.Station;

public class StationResponse {

    private Long id;
    private String name;

    private StationResponse() {
    }

    public StationResponse(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse from(final Station station) {
        final Long id = station.getId();
        final String name = station.getName();

        return new StationResponse(id, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
