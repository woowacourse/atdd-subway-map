package wooteco.subway.dao.dto;

import wooteco.subway.domain.Station;

public class StationDto {

    private final long id;
    private final String name;

    public StationDto(Station station) {
        this(0L, station.getName());
    }

    public StationDto(long id, String name) {
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
