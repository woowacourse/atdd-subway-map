package wooteco.subway.dto.response;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.domain.Line;

public class LineResponseDto {

    private Long id;
    private String name;
    private String color;
    private List<StationResponseDto> stations;

    public LineResponseDto() {
    }

    public LineResponseDto(final Long id,
                           final String name,
                           final String color,
                           final List<StationResponseDto> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public LineResponseDto(final Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = line.getStations().stream()
                .map(station -> new StationResponseDto(station))
                .collect(Collectors.toList());
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
