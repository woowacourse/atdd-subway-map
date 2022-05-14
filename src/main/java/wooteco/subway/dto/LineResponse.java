package wooteco.subway.dto;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.StationSeries;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public LineResponse() {
        this(null, null, null, null);
    }

    public static LineResponse from(Line line) {
        return new LineResponse(
            line.getId(),
            line.getName(),
            line.getColor(),
            StationSeries.fromSectionsAsOrdered(line.getSections())
                .getStations()
                .stream()
                .map(StationResponse::from)
                .collect(
                    Collectors.toList()) // TODO
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

    public List<StationResponse> getStations() {
        return stations;
    }
}
