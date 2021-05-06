package wooteco.subway.line.repository.dto;

import wooteco.subway.line.domain.Line;
import wooteco.subway.station.controller.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;

public class LineDto {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    ;

    public LineDto() {
    }

    public LineDto(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineDto(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineDto from(Line line) {
        return new LineDto(line.getId(), line.getName(), line.getColor(), new ArrayList<>());
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
