package wooteco.subway.station.dao.dto;

import wooteco.subway.station.domain.Station;

public class StationDto {

    private Long id;
    private String name;

    public StationDto() {
    }

    public StationDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationDto from(Station station) {
        return new StationDto(station.getId(), station.getName());
    }

    public StationDto(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
