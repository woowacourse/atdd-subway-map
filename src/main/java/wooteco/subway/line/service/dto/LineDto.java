package wooteco.subway.line.service.dto;

import java.util.List;
import wooteco.subway.line.domain.Line;
import wooteco.subway.station.service.dto.StationDto;

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
        return new LineDto(
                line.getId(),
                line.getName(),
                line.getColor(),
                StationDto.toListDto(line.getStations())
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
