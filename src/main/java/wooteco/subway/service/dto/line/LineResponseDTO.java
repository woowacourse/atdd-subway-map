package wooteco.subway.service.dto.line;

import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.station.StationResponseDto;

import java.util.List;

public class LineResponseDTO {

    private Long id;
    private String name;
    private String color;
    private List<StationResponseDto> stations;

    private LineResponseDTO() {
    }

    public LineResponseDTO(Line line, List<StationResponseDto> stations) {
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

    public List<StationResponseDto> getStations() {
        return stations;
    }
}
