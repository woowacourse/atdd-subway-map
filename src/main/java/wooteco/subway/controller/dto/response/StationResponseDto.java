package wooteco.subway.controller.dto.response;

import wooteco.subway.domain.Station;

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
