package wooteco.subway.controller.dto.response.line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import wooteco.subway.controller.dto.response.station.StationResponseDto;

public class LineWithAllStationsInOrderResponseDto {
    private Long id;
    private String name;
    private String color;
    private List<StationResponseDto> stations;

    public LineWithAllStationsInOrderResponseDto() {
    }

    public LineWithAllStationsInOrderResponseDto(Long id, String name, String color, List<StationResponseDto> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = new ArrayList<>(stations);
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
        return Collections.unmodifiableList(stations);
    }
}
