package wooteco.subway.dto;

import wooteco.subway.domain.Line;

import java.util.ArrayList;
import java.util.List;

public class LineAndStationsResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineAndStationsResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public LineAndStationsResponse(Line line) {
        this(line.getId(), line.getName(), line.getColor(), new ArrayList<>());
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
