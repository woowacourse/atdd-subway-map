package wooteco.subway.line.dto;

import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineEntity;
import wooteco.subway.station.dto.StationResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponse() {
    }

    public LineResponse(Line line) {
        this(line.getId(), line.getName(), line.getColor(), line.stationRoute()
                                                                .stream()
                                                                .map(StationResponse::new)
                                                                .collect(Collectors.toList()));
    }

    public LineResponse(LineEntity lineEntity) {
        this(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), Collections.emptyList());
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
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

    public List<StationResponse> getStations() {
        return stations;
    }
}
