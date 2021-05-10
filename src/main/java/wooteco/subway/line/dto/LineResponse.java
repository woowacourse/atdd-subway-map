package wooteco.subway.line.dto;

import wooteco.subway.line.domain.Line;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationDto;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationDto> stations;

    public LineResponse() {
    }

    public LineResponse(Line line) {
        this(line.id(), line.name(), line.color(), line.stations());
    }

    public LineResponse(Long id, String name, String color, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations.stream()
                .map(station -> new StationDto(station.id(), station.name()))
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

    public List<StationDto> getStations() {
        return stations;
    }
}
