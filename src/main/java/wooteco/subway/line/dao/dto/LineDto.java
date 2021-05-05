package wooteco.subway.line.dao.dto;

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

    public static LineDto from(Line line) {
        return new LineDto(line.getId(), line.getName(), line.getColor(), new ArrayList<>());
    }

    public LineDto(String name, String color) {
        this(0L, name, color, new ArrayList<>());
    }

    public LineDto(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public LineDto() {
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
