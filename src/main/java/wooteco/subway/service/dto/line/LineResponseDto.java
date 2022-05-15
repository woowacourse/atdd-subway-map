package wooteco.subway.service.dto.line;

import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.station.StationResponseDto;

import java.util.ArrayList;
import java.util.List;

public class LineResponseDto {
    private Long id;
    private String name;
    private String color;
    private List<StationResponseDto> stations;

    private LineResponseDto() {
    }

    public LineResponseDto(Line line, List<StationResponseDto> stations) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = stations;
    }

    public LineResponseDto(Line line) {
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

    public List<StationResponseDto> getStations() {
        return stations;
    }

    @Override
    public String toString() {
        return "LineResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", stations=" + stations +
                '}';
    }
}
