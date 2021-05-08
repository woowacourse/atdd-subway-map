package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.station.StationDto;

public class LineDto {

    private final Long id;
    private final String name;
    private final String color;
    private final List<StationDto> stations;

    private LineDto(final Long id, final String name, final String color, final List<StationDto> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineDto of(final Line line) {
        final List<StationDto> stationDtos = line.getStations().stream()
                .map(StationDto::of)
                .collect(Collectors.toList());
        return new LineDto(
                line.getId(),
                line.getName(),
                line.getColor(),
                stationDtos
        );
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
