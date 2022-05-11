package wooteco.subway.controller.dto;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse from(Line line) {
        List<StationResponse> responses = line.findOrderedStations().stream()
            .map(StationResponse::from)
            .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), responses);
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

    public List<StationResponse> getStations() {
        return stations;
    }
}
