package wooteco.subway.station.controller.dto;

import wooteco.subway.station.domain.Station;

public class StationDto {

    private final Long id;
    private final String name;

    private StationDto(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static StationDto of(final Station station) {
        return new StationDto(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
