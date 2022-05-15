package wooteco.subway.dto;

import java.util.List;
import wooteco.subway.domain.Line;

public class LineDto {
    private Long id;
    private String name;
    private String color;
    private List<StationDto> stations;

    public LineDto(Line line, List<StationDto> stations) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
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

    public List<StationDto> getStations() {
        return stations;
    }
}
