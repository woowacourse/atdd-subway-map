package wooteco.subway.dto.station;

import wooteco.subway.domain.Station;

public class StationResponse {

    private Long id;
    private String name;

    private StationResponse() {
    }

    private StationResponse(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse from(final Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
