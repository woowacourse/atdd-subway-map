package wooteco.subway.station.repository.dto;

import wooteco.subway.station.domain.Station;

public class StationDto {

    private Long id;
    private String name;

    public StationDto() {
    }

    public StationDto(final String name) {
        this(null, name);
    }

    public StationDto(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static StationDto from(final Station station) {
        return new StationDto(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
