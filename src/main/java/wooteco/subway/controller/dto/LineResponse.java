package wooteco.subway.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import wooteco.subway.domain.Line;

import java.util.List;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public LineResponse(final Line line, final List<StationResponse> stations) {
        this(line.getId(), line.getName(), line.getColor(), stations);
    }

    @JsonCreator
    public LineResponse(final Long id, final String name, final String color, final List<StationResponse> stations) {
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

    public List<StationResponse> getStations() {
        return stations;
    }
}
