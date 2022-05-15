package wooteco.subway.dto.response;

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static StationResponse from(final Station station) {
        return new StationResponse(station.getId(), station.getName());
    }
}
