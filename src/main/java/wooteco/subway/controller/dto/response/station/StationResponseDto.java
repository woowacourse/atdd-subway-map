package wooteco.subway.controller.dto.response.station;

import wooteco.subway.domain.station.Station;

public class StationResponseDto {
    private Long id;
    private String name;

    public StationResponseDto() {
    }

    public StationResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public StationResponseDto(Station station) {
        this(station.getId(), station.getName());
    }

    public StationResponseDto(Long id, Station station) {
        this(id, station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
