package wooteco.subway.service.dto.line;

import wooteco.subway.controller.dto.station.StationResponse;
import wooteco.subway.domain.Line;

import java.util.ArrayList;
import java.util.List;

public class LineResponseDTO {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponseDTO() {
    }

    public LineResponseDTO(Line line, List<StationResponse> stations) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = stations;
    }

    public LineResponseDTO(Line line) {
        this(line, new ArrayList<>());
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
