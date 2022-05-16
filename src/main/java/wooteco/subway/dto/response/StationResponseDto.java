package wooteco.subway.dto.response;

import wooteco.subway.domain.Station;

public class StationResponseDto {

    private Long id;
    private String name;

    public StationResponseDto() {
    }

    public StationResponseDto(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public StationResponseDto(final Station station) {
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
