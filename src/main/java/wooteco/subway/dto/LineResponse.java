package wooteco.subway.dto;

import wooteco.subway.domain.line.Line;

import java.util.List;

public class LineResponse {
    private long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
    }

    public LineResponse(long id, Line line) {
        this.id = id;
        this.name = line.getName();
        this.color = line.getColor();
    }

    public LineResponse(long id, Line line, List<StationResponse> stations) {
        this.id = id;
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = stations;
    }

    public LineResponse(Line line, List<StationResponse> stations) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = stations;
    }

    public long getId() {
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
