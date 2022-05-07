package wooteco.subway.service.dto.line;

import java.util.List;

import wooteco.subway.service.dto.station.StationResponseDto;

public class LineResponseDto {

    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponseDto> stations;

    public LineResponseDto(Long id, String name, String color, List<StationResponseDto> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponseDto> getStations() {
        return stations;
    }
}
