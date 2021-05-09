package wooteco.subway.line.dto;

import wooteco.subway.line.Line;
import wooteco.subway.station.dto.StationResponse;

import java.util.Collections;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponse() {
    }

    public LineResponse(Line line, List<StationResponse> stations) {
        this(line.getId(), line.getName(), line.getColor(), stations);
    }

    public LineResponse(Long id, String name, String color) {
        this(id, name, color, Collections.emptyList());
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
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
