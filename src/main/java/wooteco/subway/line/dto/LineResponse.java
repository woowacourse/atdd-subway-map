package wooteco.subway.line.dto;

import wooteco.subway.station.dto.Station;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<Station> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<wooteco.subway.station.domain.Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations.stream()
                .map(station -> new Station(station.getId(), station.getName()))
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

    public List<Station> getStations() {
        return stations;
    }
}
