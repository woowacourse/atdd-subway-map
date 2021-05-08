package wooteco.subway.controller.response;

import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.LineDto;

import java.util.List;

public class LineResponse {
    private Long id;
    private String color;
    private String name;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Line line) {
        this(line.getId(), line.getColor(), line.getName());
    }

    public LineResponse(LineDto lineDto) {
        this(lineDto.getId(), lineDto.getColor(), lineDto.getName());
    }

    public LineResponse(Long id, String color, String name) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    public LineResponse(Long id, String color, String name,  List<StationResponse> stations) {
        this.id = id;
        this.color = color;
        this.name = name;
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
