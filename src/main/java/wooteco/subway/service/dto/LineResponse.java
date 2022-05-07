package wooteco.subway.service.dto;

import wooteco.subway.domain.Line;

import java.util.ArrayList;

import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponse() {
    }

    public LineResponse(final Long id, final String name, final String color, final List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public LineResponse(final Long id, final String name, final String color) {
        this(id, name, color, new ArrayList<>());
    }

    public static LineResponse of(final Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor());
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
