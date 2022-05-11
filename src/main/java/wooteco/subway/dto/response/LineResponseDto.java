package wooteco.subway.dto.response;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.domain.Station;

public class LineResponseDto {

    private Long id;
    private String name;
    private String color;
    private List<StationResponseDto> stations;

    public LineResponseDto() {
    }

    public LineResponseDto(final Long id, final String name, final String color, final List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = convertToStationDtos(stations);
    }

    private List<StationResponseDto> convertToStationDtos(final List<Station> stations) {
        return stations.stream()
                .map(station -> new StationResponseDto(station.getId(), station.getName()))
                .collect(Collectors.toList());
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
