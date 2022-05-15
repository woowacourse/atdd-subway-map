package wooteco.subway.dto;

import wooteco.subway.domain.Station;

public class StationDto {

    private final Long id;
    private final String name;

    public StationDto(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
