package wooteco.subway.service.dto;

import wooteco.subway.domain.Station;

public class StationDto {
    private Long id;
    private String name;

    public StationDto () {

    }

    public StationDto(Station station) {
        this(station.getId(), station.getName());
    }

    public StationDto(Long id, String name) {
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
