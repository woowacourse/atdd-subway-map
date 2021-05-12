package wooteco.subway.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import wooteco.subway.controller.dto.StationDto;
import wooteco.subway.domain.Line;

public class LineFindResponseDto {

    private final long id;
    private final String name;
    private final String color;
    private final List<StationDto> stationDtos;

    public LineFindResponseDto(Line line, List<StationDto> stationDtos) {
        this(line.getId(), line.getName(), line.getColor(), stationDtos);
    }

    public LineFindResponseDto(long id, String name, String color, List<StationDto> stationDtos) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stationDtos = new ArrayList<>(stationDtos);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @JsonProperty("stations")
    public List<StationDto> getStationDtos() {
        return stationDtos;
    }
}
