package wooteco.subway.dto.response;

import java.util.List;

public class LineResponseDto {

    private Long id;
    private String name;
    private String color;
    private List<StationResponseDto> stations;

    public LineResponseDto() {
    }

    public LineResponseDto(Long id, String name, String color, List<StationResponseDto> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
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
